plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    id(libs.plugins.kotlin.kapt.get().pluginId)
}

android {
    namespace = "com.mifos.feature.auth"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        compileSdkPreview = "UpsideDownCake"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
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

    implementation(project(":core:designsystem"))
    implementation(project(":core:network"))
    implementation(project(":core:datastore"))
    implementation(project(":core:common"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit4)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)

    // Hilt dependency
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    //rxjava dependencies
    implementation(libs.rxandroid)
    implementation(libs.rxjava)

    // Jetpack Compose
    implementation(libs.androidx.material)
    implementation(libs.androidx.compiler)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.activity.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.androidx.material.icons.extended)

    // ViewModel utilities for Compose
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.androidx.hilt.navigation.compose)

    // fineract sdk dependencies
    implementation(libs.mifos.android.sdk.arch)

    // sdk client
    implementation(libs.fineract.client)
}