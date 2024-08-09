plugins {
    alias(libs.plugins.mifos.android.feature)
    alias(libs.plugins.mifos.android.library.compose)
    alias(libs.plugins.mifos.android.library.jacoco)
}

android {
    namespace = "com.mifos.feature.savings"
}

dependencies {
    implementation(projects.core.domain)
    testImplementation(libs.hilt.android.testing)
    testImplementation(projects.core.testing)

    androidTestImplementation(projects.core.testing)

    //material
    implementation (libs.androidx.material.v168)
}