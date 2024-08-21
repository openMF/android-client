plugins {
    alias(libs.plugins.mifos.android.feature)
    alias(libs.plugins.mifos.android.library.compose)
    alias(libs.plugins.mifos.android.library.jacoco)
}

android {
    namespace = "com.mifos.feature.passcode"
}

dependencies {
    implementation(projects.core.domain)

    //DBFlow dependencies
    kapt(libs.dbflow.processor)
    implementation(libs.dbflow)
    kapt(libs.github.dbflow.processor)
    testImplementation(libs.hilt.android.testing)
    testImplementation(projects.core.testing)

    androidTestImplementation(projects.core.testing)

    // passcode dependency
    implementation("com.github.openMF.mifos-passcode:compose:1.0.3")
}