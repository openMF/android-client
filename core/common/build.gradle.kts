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
    alias(libs.plugins.mifos.kmp.library)
    alias(libs.plugins.mifos.android.hilt)
//    alias(libs.plugins.mifos.android.library.jacoco)
//    alias(libs.plugins.secrets)
    id(libs.plugins.kotlin.parcelize.get().pluginId)
}

android {
    namespace = "com.mifos.core.common"

    buildFeatures {
        buildConfig = true
    }
}

//secrets {
//    defaultPropertiesFileName = "secrets.defaults.properties"
//}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            api(libs.coil.kt)
            api(libs.coil.core)
            api(libs.coil.svg)
            api(libs.coil.network.ktor)
            api(libs.kermit.logging)
            api(libs.squareup.okio)
            api(libs.jb.kotlin.stdlib)
            api(libs.kotlinx.datetime)
        }

        androidMain.dependencies {
            implementation(libs.kotlinx.coroutines.android)
        }
        commonTest.dependencies {
            implementation(libs.kotlinx.coroutines.test)
        }
        iosMain.dependencies {
            api(libs.kermit.simple)
        }
        desktopMain.dependencies {
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.kotlin.reflect)
        }
        jsMain.dependencies {
            api(libs.jb.kotlin.stdlib.js)
            api(libs.jb.kotlin.dom)
        }
    }
}
//dependencies {
//    implementation(projects.core.model)
//    testImplementation(libs.kotlinx.coroutines.test)
//    testImplementation(libs.turbine)
//
//    androidTestImplementation(libs.androidx.junit)
//    androidTestImplementation(libs.androidx.test.espresso.core)
//
//    implementation(libs.converter.gson)
//
//    implementation(libs.javax.inject)
//}