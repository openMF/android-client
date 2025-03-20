plugins {
    alias(libs.plugins.mifos.android.feature)
    alias(libs.plugins.mifos.android.library.compose)
    alias(libs.plugins.mifos.android.library.jacoco)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.mifos.feature.passcode"
}

dependencies {
    implementation(projects.core.domain)

    //DBFlow dependencies
    ksp(libs.dbflow.processor)
    implementation(libs.dbflow)
    ksp(libs.github.dbflow.processor)
    testImplementation(libs.hilt.android.testing)
    testImplementation(projects.core.testing)

    androidTestImplementation(projects.core.testing)

    // passcode dependency
    implementation("com.github.openMF.mifos-passcode:compose:1.0.3")
}