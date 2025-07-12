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
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.mifos.kmp.room)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.mifos.core.database"

    defaultConfig {
        testInstrumentationRunner = "com.mifos.core.testing.MifosTestRunner"
    }
}

kotlin{
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            api(projects.core.common)
            api(projects.core.model)
        }

        androidMain.dependencies {
            implementation(libs.koin.android)
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
        }

        nativeMain.dependencies {
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
        }

        desktopMain.dependencies {
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
        }
    }
}
