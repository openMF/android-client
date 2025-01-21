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
    id(libs.plugins.kotlin.parcelize.get().pluginId)
    id("kotlinx-serialization")
}

android {
    namespace = "com.mifos.core.datastore"

    defaultConfig {
        consumerProguardFiles("consumer-proguard-rules.pro")
    }

}

dependencies {
    kotlin {
        sourceSets {
            commonMain.dependencies {
                implementation(libs.multiplatform.settings)
                implementation(libs.multiplatform.settings.serialization)
                implementation(libs.multiplatform.settings.coroutines)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.core)
               // implementation(projects.core.common)
            }
        }
    }
}