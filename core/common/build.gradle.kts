plugins {
    alias(libs.plugins.mifos.android.library)
    alias(libs.plugins.mifos.android.library.jacoco)
    alias(libs.plugins.secrets)
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

dependencies {
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    
    implementation(libs.converter.gson)
}