/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
plugins {
    alias(libs.plugins.cmp.feature.convention)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.mifos.feature.path.tracking"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // RULE-FEATURE-USES-ONLY-CORE — feature modules only depend on
            // projects.core.*, never projects.coreBase.* or projects.core.network.
            implementation(projects.core.common)
            implementation(projects.core.model)
            implementation(projects.core.data)
            implementation(projects.core.designsystem)
            implementation(projects.core.ui)

            implementation(compose.ui)
            implementation(compose.material3)
            implementation(compose.foundation)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.kotlinx.serialization.json)
        }

        androidMain.dependencies {
            // Android-only embedded Google Map preview. Genuinely platform-bound —
            // the cross-platform map primitive is pending (see desktop/native/js
            // actuals which stub the map row).
            implementation(libs.maps.compose)
            implementation(libs.accompanist.permission)
        }
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}
