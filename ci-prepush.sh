#!/bin/bash

# Check if gradlew exists in the project
if [ ! -f "./gradlew" ]; then
    echo "Error: gradlew not found in the project."
    exit 1
fi

echo "Starting all checks and tests..."

failed_tasks=()
successful_tasks=()

run_gradle_task() {
    echo "Running: $1"
    "./gradlew" $1
    if [ $? -ne 0 ]; then
        echo "Warning: Task $1 failed"
        failed_tasks+=("$1")
    else
        echo "Task $1 completed successfully"
        successful_tasks+=("$1")
    fi
}

# Re-baseline dependency guard using CI-compatible resolution (all .local=false).
# Without this, a developer running with .local=true (local composite builds) would
# commit a baseline that references project :kmp-toolkit:* instead of the published
# Maven artifact — causing CI to fail even though nothing really changed.
rebaseline_dependencies() {
    local props="lib-integrate.properties"
    if [ ! -f "$props" ]; then
        echo "lib-integrate.properties not found — running dependencyGuardBaseline as-is"
        run_gradle_task "dependencyGuardBaseline"
        return
    fi

    # Check whether any entry is currently .local=true
    if grep -q "\.local=true" "$props"; then
        echo "Detected .local=true in $props — temporarily flipping to false for CI-compatible baseline"
        # Backup and replace all .local=true with .local=false
        cp "$props" "${props}.prepush.bak"
        sed -i.bak 's/\.local=true/.local=false/g' "$props"
        rm -f "${props}.bak"

        run_gradle_task "dependencyGuardBaseline"

        # Restore original (skip-worktree keeps this off the index, but restore the content)
        mv "${props}.prepush.bak" "$props"
        echo "Restored $props to original state"
    else
        # All entries already .local=false — safe to baseline directly
        run_gradle_task "dependencyGuardBaseline"
    fi
}

tasks=(
    "check -p build-logic"
    "spotlessApply --no-configuration-cache"
    "detekt"
    ":cmp-android:build"
    # Read-only check (no project-dir write -> Gradle 9 implicit-dependency safe).
    # Refresh the golden manually with `./gradlew :cmp-android:updateProdReleaseBadging`
    # when manifest/permission/locale changes are intentional, then commit.
    ":cmp-android:checkProdReleaseBadging"
)

for task in "${tasks[@]}"; do
    run_gradle_task "$task"
done

# Run dependency baseline last (after build succeeds) so the resolved graph is warm
rebaseline_dependencies

echo "All tasks have finished."

echo "Successful tasks:"
for task in "${successful_tasks[@]}"; do
    echo "- $task"
done

if [ ${#failed_tasks[@]} -eq 0 ]; then
    echo "All checks and tests completed successfully."
else
    echo "Failed tasks:"
    for task in "${failed_tasks[@]}"; do
        echo "- $task"
    done
    echo "Please review the output above for more details on the failures."
fi

echo "Total tasks: ${#tasks[@]}"
echo "Successful tasks: ${#successful_tasks[@]}"
echo "Failed tasks: ${#failed_tasks[@]}"