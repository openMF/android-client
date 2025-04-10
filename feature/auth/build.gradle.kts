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
}

android {
    namespace = "com.mifos.feature.auth"
}

kotlin {
    sourceSets {

        commonMain.dependencies {
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.material3)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(libs.jb.kotlin.stdlib)
            implementation(libs.kotlin.reflect)
            implementation(projects.core.ui)
            implementation(projects.core.common)
        }
    }
}

dependencies {
    testImplementation(libs.hilt.android.testing)
    testImplementation(projects.core.testing)
    implementation(projects.core.data)
    implementation(projects.core.domain)

    androidTestImplementation(projects.core.testing)
}
