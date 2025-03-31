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
    alias(libs.plugins.mifos.android.library.jacoco)
    alias(libs.plugins.mifos.android.koin)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.mifos.core.domain"
}

//dependencies {
//    api(projects.core.data)
//    api(projects.core.model)
//
//    implementation(libs.javax.inject)
//
//    implementation(libs.dbflow)
//
//    // sdk client
////    implementation(libs.fineract.client)
//
//    implementation(libs.rxandroid)
//    implementation(libs.rxjava)
//
//    implementation(libs.squareup.okhttp)
//
//    implementation(libs.androidx.paging.runtime.ktx)
//
//    testImplementation(projects.core.testing)
//    testImplementation (libs.androidx.paging.common.ktx)
//    testImplementation (libs.androidx.paging.testing)
//}

kotlin {
    sourceSets {
        commonMain.dependencies {
          //  api(projects.core.data)
            api(projects.core.model)
          //  api(projects.core.common)
          //  api(projects.core.network)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.ktor.client.core)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
        nativeMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        desktopMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
    }
}

compose.resources {
    publicResClass = true
    generateResClass = always
    packageOfResClass = "core.domain.generated.resources"
}