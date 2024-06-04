plugins {
    alias(libs.plugins.mifos.android.feature)
    alias(libs.plugins.mifos.android.library.compose)
    alias(libs.plugins.mifos.android.library.jacoco)
}

android {
    namespace = "com.mifos.feature.settings"
}

dependencies {

    implementation(projects.core.datastore)
    implementation(projects.core.domain)

    androidTestImplementation(libs.androidx.compose.ui.test)
    debugApi(libs.androidx.compose.ui.testManifest)

    testImplementation(libs.hilt.android.testing)
    testImplementation(projects.core.testing)

    androidTestImplementation(projects.core.testing)
}