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
    namespace = "com.mifos.feature.search"
}

//dependencies {
//
//    implementation(projects.core.domain)
//
//    implementation(libs.accompanist.drawablepainter)
//
//    // Text drawable dependency
//    implementation(libs.textdrawable)
//
//    androidTestImplementation(libs.androidx.compose.ui.test)
//    debugApi(libs.androidx.compose.ui.test.manifest)
//
//    testImplementation(libs.hilt.android.testing)
//    testImplementation(projects.core.testing)
//
//    androidTestImplementation(projects.core.testing)
//}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(compose.ui)
            api(projects.core.common)
            api(projects.core.model)
            api(projects.core.domain)

        }
    }
}