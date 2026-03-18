/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
plugins {
    alias(libs.plugins.kmp.library.convention)
    alias(libs.plugins.cmp.feature.convention)
    alias(libs.plugins.kmp.koin.convention)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "cmp.shared"
}

kotlin {
    listOf(
// removed support for iosX64 to align with Jetbrains deprecation of the macosX64 targets
//        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            optimized = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            // Navigation Modules
            implementation(projects.cmpNavigation)
            implementation(compose.components.resources)
            api(projects.core.data)
            api(projects.core.network)
            //put your multiplatform dependencies here
            implementation(compose.material)
            implementation(compose.material3)
            api(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
        }

        desktopMain.dependencies {
            // Desktop specific dependencies
            implementation(compose.desktop.currentOs)
            implementation(compose.desktop.common)
        }
    }
}

// Compatibility task for CI: some templates expect a task named `generateDummyFramework`.
// Create a task that depends on linking the ComposeApp framework for all iOS targets.
val generateDummyFramework by tasks.registering {
    group = "build"
    description = "Compatibility task to generate the ComposeApp dummy framework for iOS targets (used by CI)."
    doLast {
        println("generateDummyFramework: running compatibility task. If iOS frameworks are configured, they will be built by named tasks.")
    }
}

// Wire up dependencies dynamically if the Kotlin/Native targets create linkFramework tasks
kotlin.targets.filter { it.name.startsWith("ios") }.forEach { target ->
    val targetNameCapitalized = target.name.replaceFirstChar { it.uppercaseChar() }
    // The task name pattern for linking a framework is 'link${TargetName}Framework', e.g., linkIosArm64Framework
    val linkTaskName = "link${targetNameCapitalized}Framework"
    tasks.findByName(linkTaskName)?.let { linkTask ->
        generateDummyFramework.configure { dependsOn(linkTask) }
    }
}
