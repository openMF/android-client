plugins {
    alias(libs.plugins.mifos.android.library)
    alias(libs.plugins.mifos.android.library.compose)
    alias(libs.plugins.mifos.android.library.jacoco)
}

android {
    namespace = "com.mifos.core.ui"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    api(libs.androidx.metrics)
    api(projects.core.designsystem)
    api(projects.core.model)
    api(projects.core.testing)

    implementation(libs.coil.kt.compose)
    implementation(libs.androidx.paging.compose)

    androidTestImplementation(projects.core.testing)
}