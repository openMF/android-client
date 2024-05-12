import org.gradle.api.tasks.testing.logging.TestLogEvent

/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.androidx.navigation)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.secrets)
    id(libs.plugins.kotlin.parcelize.get().pluginId)
    id(libs.plugins.kotlin.kapt.get().pluginId)
}

apply(from = "../config/quality/quality.gradle")

android {
    namespace = "com.mifos.mifosxdroid"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mifos.mifosxdroid"
        minSdk = 26
        targetSdk = 34
        versionCode = 6
        versionName = "1.0.1"

        multiDexEnabled = true
        compileSdkPreview = "UpsideDownCake"
        // A test runner provided by https://code.google.com/p/android-test-kit/
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    signingConfigs {
        create("release") {
            storeFile = file("../default_key_store.jks")
            storePassword = "mifos1234"
            keyAlias = "mifos"
            keyPassword = "mifos1234"
        }
    }

    buildTypes {

        debug {
            isMinifyEnabled = false
            // TODO  Uses new built-in shrinker, To Enable update buils tools to 2.2
            // TODO http://tools.android.com/tech-docs/new-build-system/built-in-shrinker
            //useProguard false
            //proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
            //testProguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguardTest-rules.pro'
        }

        release {
            isMinifyEnabled = false
            isDebuggable = false

            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            testProguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguardTest-rules.pro"
            )
        }
    }

    lint {
        abortOnError = false
        disable += "InvalidPackage"
        disable += "MissingTranslation"
        disable += "OutdatedLibrary"
    }

    // Exclude duplicated Hamcrest LICENSE.txt from being packaged into the apk.
    // This is a workaround for https://code.google.com/p/android/issues/detail?id=65445.
    // The Hamcrest is used in tests.
    packaging {
        resources.excludes.add("LICENSE.txt")
        resources.excludes.add("META-INF/LICENSE.txt")
        resources.excludes.add("META-INF/dbflow-kotlinextensions-compileReleaseKotlin.kotlin_module")
    }

    useLibrary("org.apache.http.legacy")

    // Always show the result of every unit test, even if it passes.
    testOptions.unitTests.all {
        it.apply {
            testLogging.events = setOf(
                TestLogEvent.PASSED,
                TestLogEvent.SKIPPED,
                TestLogEvent.FAILED,
                TestLogEvent.STANDARD_OUT,
                TestLogEvent.STANDARD_ERROR
            )
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

secrets {
    defaultPropertiesFileName = "secrets.defaults.properties"
}

dependencies {

    implementation(project(":feature:auth"))
    implementation(project(":feature:client"))
    implementation(project(":feature:checker-inbox-task"))
    implementation(project(":feature:Individual-collection-sheet"))
    implementation(project(":core:data"))
    implementation(project(":core:datastore"))
    implementation(project(":core:network"))
    implementation(project(":core:common"))

    // Multidex dependency
    implementation(libs.androidx.multidex)

    // Text drawable dependency
    implementation(libs.textdrawable)

    // Kotlin standard library
    implementation(libs.kotlin.stdlib)

    //DBFlow dependencies
    kapt(libs.dbflow.processor)
    implementation(libs.dbflow)
    kapt(libs.github.dbflow.processor)

    // App's Support dependencies, including test
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.recyclerview)
    implementation(libs.material)
    implementation(libs.play.services.places)
    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)
    implementation(libs.android.maps.utils)
    implementation(libs.androidx.espresso.idling.resource)

    //LifeCycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.extensions)
    implementation(libs.androidx.lifecycle.reactivestreams.ktx)
    implementation(libs.androidx.lifecycle.common.java8)

    //Square dependencies
    implementation("com.squareup.retrofit2:retrofit:2.9.0") {
        // exclude Retrofit’s OkHttp peer-dependency module and define your own module import
        exclude(module = "okhttp")
    }
    implementation(libs.converter.gson)
    implementation(libs.converter.scalars)
    implementation(libs.adapter.rxjava)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    implementation(libs.fliptables)

    //sweet error dependency
    implementation(libs.sweet.error)

    //rxjava dependencies
    implementation(libs.rxandroid)
    implementation(libs.rxjava)

    //stetho dependencies
    implementation(libs.stetho)
    implementation(libs.stetho.okhttp3)

    //showcase View dependency
    implementation(libs.materialshowcaseview)

    //Iconify dependency
    implementation(libs.android.iconify.material)

    //glide dependency
    implementation(libs.glide)

    //mifos passcode dependency
    implementation(libs.mifos.passcode)

    // Android Testing Support Library's runner and rules dependencies
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)

    // Espresso UI Testing dependencies.
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1") {
        exclude(group = "com.android.support", module = "appcompat")
        exclude(group = "com.android.support", module = "support-v4")
        exclude(group = "com.android.support", module = "recyclerview-v7")
        exclude(group = "com.android.support", module = "design")
    }
    androidTestImplementation(libs.androidx.espresso.intents)

    // Mockito and jUnit dependencies
    testImplementation(libs.junit4)
    testImplementation(libs.mockito.core)
    androidTestImplementation(libs.junit4)
    androidTestImplementation(libs.mockito.core)
    androidTestImplementation(libs.mockito.android)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.androidx.core.testing)

    //Android-Jobs
    implementation(libs.android.job)

    // androidx annotations
    implementation(libs.androidx.annotation)

    //preferences
    implementation(libs.androidx.preference.ktx)

    //Splash Screen
    implementation(libs.androidx.core.splashscreen)

    // Navigation Components
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Hilt dependency
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // fineract sdk dependencies
    implementation(libs.mifos.android.sdk.arch)

    // sdk client
    implementation(libs.fineract.client)

    // Jetpack Compose
    implementation(libs.androidx.material)
    implementation(libs.androidx.compiler)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.activity.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.androidx.compose.material.iconsExtended)

    // ViewModel utilities for Compose
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.androidx.hilt.navigation.compose)
}