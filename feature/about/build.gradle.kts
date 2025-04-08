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
//    alias(libs.plugins.mifos.android.feature)
//    alias(libs.plugins.mifos.android.library.compose)
//    alias(libs.plugins.mifos.android.library.jacoco)
    alias(libs.plugins.mifos.cmp.feature)
}

android {
    namespace = "com.mifos.feature.about"
}
kotlin {
    sourceSets {
        commonMain.dependencies {
           api(projects.core.common)
        }
    }
}
dependencies {
    implementation(libs.androidx.ui.android)
    implementation(project(":core:ui"))
    implementation(libs.androidx.material3.android)
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation(libs.androidx.foundation.android)
}
