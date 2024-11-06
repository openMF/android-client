plugins {
    alias(libs.plugins.mifos.android.library)
    alias(libs.plugins.mifos.android.hilt)
    alias(libs.plugins.mifos.android.library.jacoco)
    alias(libs.plugins.secrets)
}
detekt {
    buildUponDefaultConfig = true // preconfigure defaults
    allRules = false // activate all available (even unstable) rules.
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
    implementation(projects.core.model)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
  //  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)

    implementation(libs.converter.gson)

    implementation(libs.javax.inject)
}