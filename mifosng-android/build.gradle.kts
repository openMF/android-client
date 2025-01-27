/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
import org.mifos.MifosBuildType
import org.mifos.dynamicVersion

plugins {
    alias(libs.plugins.mifos.android.application)
    alias(libs.plugins.mifos.android.application.compose)
    alias(libs.plugins.mifos.android.application.flavors)
    alias(libs.plugins.mifos.android.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.secrets)
    alias(libs.plugins.androidx.navigation)
    alias(libs.plugins.gms)
}

android {
    namespace = "com.mifos.mifosxdroid"

    defaultConfig {
        applicationId = "com.mifos.mifosxdroid"
        versionName = project.dynamicVersion
        versionCode = System.getenv("VERSION_CODE")?.toIntOrNull() ?: 1
        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
    }

    signingConfigs {
        create("release") {
            storeFile =
                file(System.getenv("KEYSTORE_PATH") ?: "../keystores/release_keystore.keystore")
            storePassword = System.getenv("KEYSTORE_PASSWORD") ?: "mifos1234"
            keyAlias = System.getenv("KEYSTORE_ALIAS") ?: "mifos"
            keyPassword = System.getenv("KEYSTORE_ALIAS_PASSWORD") ?: "mifos1234"
            enableV1Signing = true
            enableV2Signing = true
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = MifosBuildType.DEBUG.applicationIdSuffix
        }

        // TODO:: Fix the proguard rules for release build
        release {
            isMinifyEnabled = false
            isDebuggable = true
            isShrinkResources = false
            applicationIdSuffix = MifosBuildType.RELEASE.applicationIdSuffix
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            testProguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguardTest-rules.pro",
            )
        }
    }

    // Exclude duplicated Hamcrest LICENSE.txt from being packaged into the apk.
    // This is a workaround for https://code.google.com/p/android/issues/detail?id=65445.
    // The Hamcrest is used in tests.
    packaging {
        resources.excludes.add("LICENSE.txt")
        resources.excludes.add("META-INF/LICENSE.txt")
        resources.excludes.add("META-INF/dbflow-kotlinextensions-compileReleaseKotlin.kotlin_module")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

secrets {
    defaultPropertiesFileName = "secrets.defaults.properties"
}

dependencies {
    implementation(projects.feature.offline)
    implementation(projects.feature.savings)
    implementation(projects.feature.note)
    implementation(projects.feature.auth)
    implementation(projects.feature.client)
    implementation(projects.feature.checkerInboxTask)
    implementation(projects.feature.collectionSheet)
    implementation(projects.feature.groups)
    implementation(projects.feature.settings)
    implementation(projects.feature.center)
    implementation(projects.feature.about)
    implementation(projects.feature.report)
    implementation(projects.feature.pathTracking)
    implementation(projects.feature.activate)
    implementation(projects.feature.loan)
    implementation(projects.feature.document)
    implementation(projects.feature.dataTable)
    implementation(projects.feature.search)

    implementation(projects.libs.mifosPasscode)

    implementation(projects.core.common)
    implementation(projects.core.ui)
    implementation(projects.core.designsystem)
    implementation(projects.core.data)
    implementation(projects.core.model)
    implementation(projects.core.network)
    implementation(projects.core.datastore)
    implementation(projects.core.database)

    // Multidex dependency
    implementation(libs.androidx.multidex)

    // App's Support dependencies, including test
    implementation(libs.androidx.appcompat)

    // Mockito and jUnit dependencies
    testImplementation(libs.junit4)
    testImplementation(libs.mockito.core)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.androidx.core.testing)

    //Splash Screen
    implementation(libs.androidx.core.splashscreen)

    // Hilt dependency
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Jetpack Compose
    implementation(libs.androidx.compiler)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.activity.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.androidx.navigation.compose)

    // ViewModel utilities for Compose
    implementation(libs.androidx.hilt.navigation.compose)

    //LifeCycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
}

dependencyGuard {
    configuration("demoDebugCompileClasspath")
    configuration("demoReleaseCompileClasspath")
    configuration("prodDebugCompileClasspath")
    configuration("prodReleaseCompileClasspath")
}
