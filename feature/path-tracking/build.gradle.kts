/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
plugins {
    alias(libs.plugins.mifos.cmp.feature)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.mifos.feature.path.tracking"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.domain)
            implementation(projects.core.designsystem)

            implementation(compose.ui)
            implementation(compose.material3)
            implementation(compose.foundation)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.kotlinx.serialization.json)
        }

        androidMain.dependencies {
            implementation(libs.maps.compose)
            implementation(libs.accompanist.permission)
        }
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}