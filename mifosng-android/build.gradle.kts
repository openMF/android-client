import org.gradle.api.tasks.testing.logging.TestLogEvent

/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.serialization")
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

dependencies {

    implementation(project(":feature:auth"))

    // Multidex dependency
    implementation("androidx.multidex:multidex:2.0.1")

    // Text drawable dependency
    implementation("com.github.amulyakhare:TextDrawable:558677ea31")

    // Kotlin standard library
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")

    //DBFlow dependencies
    kapt("com.github.raizlabs.dbflow.dbflow:dbflow-processor:3.1.1")
    implementation("com.github.raizlabs.dbflow.dbflow:dbflow:3.1.1")
    kapt("com.github.raizlabs.dbflow:dbflow-processor:4.2.4")

    // App's Support dependencies, including test
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("com.google.android.gms:play-services-places:17.0.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.maps.android:android-maps-utils:0.4.2")
    implementation("androidx.test.espresso:espresso-idling-resource:3.5.1")

    //LifeCycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-reactivestreams-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.6.1")

    //Square dependencies
    implementation("com.squareup.retrofit2:retrofit:2.9.0") {
        // exclude Retrofit’s OkHttp peer-dependency module and define your own module import
        exclude(module = "okhttp")
    }
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("com.squareup.retrofit2:adapter-rxjava:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.2")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation("com.jakewharton.fliptables:fliptables:1.0.1")

    //sweet error dependency
    implementation("com.github.therajanmaurya:sweet-error:1.0.9")

    //rxjava dependencies
    implementation("io.reactivex:rxandroid:1.1.0")
    implementation("io.reactivex:rxjava:1.3.8")

    //stetho dependencies
    implementation("com.facebook.stetho:stetho:1.3.1")
    implementation("com.facebook.stetho:stetho-okhttp3:1.3.1")

    //showcase View dependency
    implementation("com.github.deano2390:MaterialShowcaseView:1.3.7")

    //Iconify dependency
    implementation("com.joanzapata.iconify:android-iconify-material:2.2.2")

    //glide dependency
    implementation("com.github.bumptech.glide:glide:4.15.1")

    //mifos passcode dependency
    implementation("com.mifos.mobile:mifos-passcode:1.0.0")

    // Android Testing Support Library's runner and rules dependencies
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")

    // Espresso UI Testing dependencies.
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1") {
        exclude(group = "com.android.support", module = "appcompat")
        exclude(group = "com.android.support", module = "support-v4")
        exclude(group = "com.android.support", module = "recyclerview-v7")
        exclude(group = "com.android.support", module = "design")
    }
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")

    // Mockito and jUnit dependencies
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.5.0")
    androidTestImplementation("junit:junit:4.13.2")
    androidTestImplementation("org.mockito:mockito-core:5.5.0")
    androidTestImplementation("org.mockito:mockito-android:5.5.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.2")
    testImplementation("androidx.arch.core:core-testing:2.2.0")

    //Android-Jobs
    implementation("com.evernote:android-job:1.2.6")

    // androidx annotations
    implementation("androidx.annotation:annotation:1.6.0")

    //preferences
    implementation("androidx.preference:preference-ktx:1.2.0")

    //Splash Screen
    implementation("androidx.core:core-splashscreen:1.1.0-alpha01")

    // Navigation Components
    implementation("androidx.navigation:navigation-fragment-ktx:2.6.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.6.0")

    // Hilt dependency
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-android-compiler:2.50")

    // fineract sdk dependencies
    implementation("com.github.openMF:mifos-android-sdk-arch:1.06")

    // sdk client
    implementation("com.github.openMF:fineract-client:2.0.3")

    // Jetpack Compose
    implementation("androidx.compose.material:material:1.6.0")
    implementation("androidx.compose.compiler:compiler:1.5.8")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.1")
    implementation("androidx.activity:activity-compose:1.8.2")
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.1")
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.compose.material:material-icons-extended:1.6.1")

    // ViewModel utilities for Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
}