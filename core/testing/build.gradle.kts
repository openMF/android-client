import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.mifos.core.testing"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "com.mifos.core.testing.MifosTestRunner"
        consumerProguardFiles("consumer-rules.pro")
        testOptions.animationsDisabled = true
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
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
    api(kotlin("test"))
    api(project(":core:data"))
    api(project(":core:datastore"))
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))

    // Hilt dependency
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-android-compiler:2.50")

    implementation("androidx.test:rules:1.5.0")
    implementation("com.google.dagger:hilt-android-testing:2.50")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.3")
    testImplementation("androidx.compose.ui:ui-test:1.6.3")
    androidTestImplementation("androidx.compose.ui:ui-test:1.6.3")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4-android:1.6.3")

    // Mockito & Junit & Turbine
    testImplementation("app.cash.turbine:turbine:1.1.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.11.0")
    androidTestImplementation("junit:junit:4.13.2")
    androidTestImplementation("org.mockito:mockito-core:5.11.0")
    androidTestImplementation("org.mockito:mockito-android:5.11.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
}