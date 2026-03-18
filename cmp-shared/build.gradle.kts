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
    // Note: removed explicit cocoapods plugin to avoid unresolved DSL in some environments.
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

    // Note: CocoaPods integration is intentionally skipped here to keep Gradle script compatible
    // across environments used by contributors and CI. Instead we generate a minimal podspec file
    // with a task below when needed by iOS workflows.

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
        // Also ensure a minimal podspec exists for CocoaPods-based iOS workflows
        val iosFolder = project.rootDir.resolve("cmp-ios")
        if (iosFolder.exists()) {
            val podspecFile = iosFolder.resolve("cmp_shared.podspec")
            if (!podspecFile.exists()) {
                podspecFile.writeText("""
                    Pod::Spec.new do |s|
                      s.name     = 'cmp_shared'
                      s.version  = '0.0.1'
                      s.summary  = 'Generated minimal podspec for cmp_shared'
                      s.homepage = 'https://github.com/openMF/android-client'
                      s.license  = { :type => 'MPL-2.0' }
                      s.author = { 'Mifos' => 'noreply@example.org' }
                      s.platforms = { :ios => '13.0' }
                      s.source = { :http => 'https://example.com/placeholder' }
                    end
                """)
                println("Generated minimal podspec at: ${podspecFile.absolutePath}")
            }
        }
    }
}

// Wire up dependencies dynamically for any link*Framework tasks that the Kotlin/Native plugin registers.
// Use tasks.matching so we attach dependencies even if tasks are created later.
tasks.matching { it.name.matches(Regex("link.*Framework")) }.configureEach {
    generateDummyFramework.configure { dependsOn(this@configureEach) }
}
