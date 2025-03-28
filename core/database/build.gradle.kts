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
    alias(libs.plugins.mifos.android.library)
    alias(libs.plugins.mifos.android.koin)
    alias(libs.plugins.mifos.android.room)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.mifos.android.library.jacoco)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.mifos.core.database"

    defaultConfig {
        testInstrumentationRunner = "com.mifos.core.testing.MifosTestRunner"
    }
}

dependencies {
    api(projects.core.model)
    api(projects.core.common)

    implementation(libs.converter.gson)
    implementation(libs.kotlinx.serialization.json)

    // fineract sdk dependencies
    implementation(libs.mifos.android.sdk.arch)

    // sdk client
    implementation(libs.fineract.client)

    androidTestImplementation(projects.core.testing)
    implementation(libs.koin.android)
}