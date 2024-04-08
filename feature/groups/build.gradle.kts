import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.mifos.feature.groups"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "com.mifos.core.testing.MifosTestRunner"
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

    packaging {
        resources {
            excludes.add("DebugProbesKt.bin")
        }
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            // Set JVM target to 11
            jvmTarget = JavaVersion.VERSION_17.toString()
            // Treat all Kotlin warnings as errors (disabled by default)
            // Override by setting warningsAsErrors=true in your ~/.gradle/gradle.properties
            val warningsAsErrors: String? by project
            allWarningsAsErrors = warningsAsErrors.toBoolean()
            freeCompilerArgs = freeCompilerArgs + listOf(
                // Enable experimental coroutines APIs, including Flow
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            )
        }
    }
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:datastore"))
    implementation(project(":core:common"))
    implementation(project(":core:network"))
    api(project(":core:ui"))

    testImplementation(project(":core:testing"))
    androidTestImplementation(project(":core:testing"))

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Hilt dependency
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-android-compiler:2.50")

    //rxjava dependencies
    implementation("io.reactivex:rxandroid:1.1.0")
    implementation("io.reactivex:rxjava:1.3.8")

    // Jetpack Compose
//    implementation("androidx.compose.material:material:1.6.0")
    implementation("androidx.compose.compiler:compiler:1.5.10")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.3")
    implementation("androidx.activity:activity-compose:1.8.2")
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.3")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.compose.material:material-icons-extended:1.6.3")

    // ViewModel utilities for Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // fineract sdk dependencies
    implementation("com.github.openMF:mifos-android-sdk-arch:1.06")

    // sdk client
    implementation("com.github.openMF:fineract-client:2.0.3")

    // coil
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Retrofit Core
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    //DBFlow dependencies
    kapt("com.github.raizlabs.dbflow.dbflow:dbflow-processor:3.1.1")
    implementation("com.github.raizlabs.dbflow.dbflow:dbflow:3.1.1")
    kapt("com.github.raizlabs.dbflow:dbflow-processor:4.2.4")

    // swipe refresh
    implementation("com.google.accompanist:accompanist-swiperefresh:0.27.0")

    // paging 3
    implementation("androidx.paging:paging-runtime-ktx:3.2.1")
    implementation("androidx.paging:paging-compose:3.2.1")
    // alternatively - without Android dependencies for tests
    testImplementation ("androidx.paging:paging-common-ktx:3.2.1")
    testImplementation ("androidx.paging:paging-testing:3.2.1")

    implementation("androidx.test:rules:1.5.0")
    implementation("com.google.dagger:hilt-android-testing:2.50")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.3")
    testImplementation("androidx.compose.ui:ui-test:1.6.3")
    androidTestImplementation("androidx.compose.ui:ui-test:1.6.3")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4-android:1.6.3")

}