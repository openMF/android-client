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
    alias(libs.plugins.mifos.kmp.koin)
    alias(libs.plugins.secrets)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.mifos.core.common"

    buildFeatures {
        buildConfig = true
    }
}

secrets {
    defaultPropertiesFileName = "secrets.defaults.properties"
}

kotlin {

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            isStatic = false
            export(libs.kermit.simple)
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.core.model)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            api(libs.coil.kt)
            api(libs.coil.core)
            api(libs.coil.svg)
            api(libs.coil.network.ktor)
            api(libs.kermit.logging)
            api(libs.squareup.okio)
            api(libs.jb.kotlin.stdlib)
            api(libs.kotlinx.datetime)
            implementation(libs.filekit.core)
            implementation(libs.filekit.coil)
            implementation(libs.filekit.compose)
            implementation(libs.filekit.dialog.compose)
            implementation(libs.ktor.client.core)
        }

        androidMain.dependencies {
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.koin.android)
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
