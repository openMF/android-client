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
    alias(libs.plugins.mifos.android.hilt)
    alias(libs.plugins.mifos.android.library.jacoco)
    alias(libs.plugins.ksp)
    id(libs.plugins.kotlin.parcelize.get().pluginId)
}

android {
    namespace = "com.mifos.core.database"

    defaultConfig {
        testInstrumentationRunner = "com.mifos.core.testing.MifosTestRunner"
    }
}

dependencies {
    api(projects.core.common)
    api(projects.core.model)
    api(projects.core.datastore)

    implementation(libs.converter.gson)

    //rxjava dependencies
    implementation(libs.rxandroid)
    implementation(libs.rxjava)

    //DBFlow dependencies
    kapt(libs.dbflow.processor)
    implementation(libs.dbflow)
    kapt(libs.github.dbflow.processor)

    //room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    kspTest(libs.androidx.room.compiler)

    // Hilt dependency
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // fineract sdk dependencies
    implementation(libs.mifos.android.sdk.arch)

    // sdk client
    implementation(libs.fineract.client)

    androidTestImplementation(projects.core.testing)
}