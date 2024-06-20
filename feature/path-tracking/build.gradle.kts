plugins {
    alias(libs.plugins.mifos.android.feature)
    alias(libs.plugins.mifos.android.library.compose)
    alias(libs.plugins.mifos.android.library.jacoco)
}

android {
    namespace = "com.mifos.feature.path_tracking"
}

dependencies {
    implementation(projects.core.domain)

    implementation(libs.androidx.material)
    implementation(libs.accompanist.permission)

    implementation(libs.coil.kt.compose)
    testImplementation(libs.hilt.android.testing)
    testImplementation(projects.core.testing)
    androidTestImplementation(projects.core.testing)

    implementation(libs.maps.compose)
}