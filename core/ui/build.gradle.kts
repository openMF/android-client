plugins {
    alias(libs.plugins.mifos.android.library)
    alias(libs.plugins.mifos.android.library.compose)
}

android {
    namespace = "com.mifos.core.ui"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    api(projects.core.designsystem)

    androidTestImplementation(projects.core.testing)
}