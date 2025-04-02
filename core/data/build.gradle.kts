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
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.mifos.kmp.library)
}

android {
    namespace = "com.mifos.core.data"

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}

kotlin{
    sourceSets{
        commonMain.dependencies {
            api(projects.core.common)
//            api(projects.core.datastore)
//            api(projects.core.network)
//            api(projects.core.database)
        }

        androidMain.dependencies {
            implementation(libs.kotlinx.serialization.json)
        }
    }
}

dependencies {

    //rxjava dependencies
    api(libs.rxandroid)
    api(libs.rxjava)

    api(libs.dbflow)
    api(libs.squareup.okhttp)

    // sdk client
    api(libs.fineract.client)

    api(libs.androidx.paging.runtime.ktx)

    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlinx.serialization.json)
    testImplementation(projects.core.testing)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.tracing.ktx)
}