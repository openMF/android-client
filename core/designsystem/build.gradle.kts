/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
plugins {
    alias(libs.plugins.kmp.library.convention)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}


kotlin {
    sourceSets {
        // fork-preserved (offline-first-template-migration 04-pilot-loan-transaction-ledger —
        // discovered while chasing the first real end-to-end compile): the full template
        // build.gradle.kts overwrite (01-template-adoption) dropped this whole block, but the
        // fork's real MifosPermissionBox.android.kt still needs
        // androidx.activity.compose.{rememberLauncherForActivityResult,ActivityResultContracts}
        // (+ transitively androidx.core.app.ActivityCompat / androidx.core.content.ContextCompat).
        androidMain.dependencies {
            implementation(libs.androidx.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
        }
        androidInstrumentedTest.dependencies {
            implementation(libs.androidx.compose.ui.test)
        }
        androidUnitTest.dependencies {
            implementation(libs.androidx.compose.ui.test)
        }
        commonMain.dependencies {
            api(projects.coreBase.designsystem)
            // Theme wires LocalScreenStateDefaults from core/store so every screen
            // wrapped by KptTheme picks up the app's branded ScreenState defaults.
            implementation(projects.core.store)

            implementation(compose.ui)
            implementation(compose.uiUtil)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation(libs.coil.kt.compose)

            // fork-preserved (offline-first-template-migration 02-store-infra-screenstate
            // T5-merge): dropped by the full core/designsystem/build.gradle.kts template
            // overwrite, but the fork's real MifosIcons.kt (FluentIcons) and
            // MifosBottomSheet.kt (BackCallback/arkivanov essenty) still need them.
            api(libs.back.handler)
            api(libs.window.size)
            implementation(libs.fluentui.system.icons)
        }
    }
}

compose.resources {
    publicResClass = true
    generateResClass = always
    packageOfResClass = "kpt.core.designsystem.generated.resources"
}