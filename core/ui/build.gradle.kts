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
        androidMain.dependencies {
            api(libs.androidx.metrics)
            implementation(libs.androidx.browser)
            implementation(libs.androidx.compose.runtime)
            implementation(compose.uiTooling)
            // fork-preserved (offline-first-template-migration 02-store-infra-screenstate
            // T5-merge): dropped by the full core/ui/build.gradle.kts template overwrite.
            implementation(libs.google.oss.licenses)
        }

        commonMain.dependencies {
            implementation(projects.core.firebase)
            implementation(projects.core.designsystem)
            implementation(projects.core.model)
            implementation(projects.core.common)
            // For rememberKptPullToRefreshState(pagingStream) bridge — observes
            // ScreenState freshness + calls pagingStream.refresh() on pull.
            implementation(projects.coreBase.store)
            // Re-export core-base/ui (ScreenContent + Store5 UI wrappers) as core/ui's public API so
            // feature modules depend on core/ui, never core-base/ui directly (encapsulation, Phase A).
            api(projects.coreBase.ui)
            implementation(libs.jb.composeViewmodel)
            implementation(libs.jb.lifecycleViewmodel)
            implementation(libs.jb.lifecycleViewmodelSavedState)
            implementation(libs.coil.kt)
            implementation(libs.coil.kt.compose)
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.jb.composeNavigation)
            implementation(libs.filekit.compose)
            implementation(libs.filekit.core)

            // fork-preserved (offline-first-template-migration 02-store-infra-screenstate
            // T5-merge): dropped by the full core/ui/build.gradle.kts template overwrite,
            // but the fork's real HtmlTemplateGenerator.kt (PDF export), signature capture,
            // image cropping, and Lottie animations still need them.
            api(libs.kotlinx.datetime)
            implementation(libs.jb.lifecycle.compose)
            implementation(libs.ktor.client.core)
            implementation(libs.compose.signature)
            implementation(libs.crop.krop.ui)
            implementation(libs.compottie.resources)
            implementation(libs.compottie.lite)
            implementation(libs.kotlinx.html)
        }
        desktopMain.dependencies {
            // fork-preserved (offline-first-template-migration 02-store-infra-screenstate
            // T5-merge): PDF rendering backend is JVM/desktop-only in the fork original.
            implementation(libs.openhtmltopdf.pdfbox)
            implementation(libs.openhtmltopdf.svg.support)
        }
        androidInstrumentedTest.dependencies {
            implementation(libs.bundles.androidx.compose.ui.test)
        }
    }
}

compose.resources {
    publicResClass = true
    generateResClass = always
    packageOfResClass = "kpt.core.ui.generated.resources"
}