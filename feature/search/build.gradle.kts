plugins {
    alias(libs.plugins.mifos.android.feature)
    alias(libs.plugins.mifos.android.library.compose)
    alias(libs.plugins.mifos.android.library.jacoco)
}

android {
    namespace = "com.mifos.feature.search"
}

dependencies {

    implementation(projects.core.domain)

    implementation(libs.accompanist.drawablepainter)

    // Text drawable dependency
    implementation(libs.textdrawable)

    androidTestImplementation(libs.androidx.compose.ui.test)
    debugApi(libs.androidx.compose.ui.testManifest)

    testImplementation(libs.hilt.android.testing)
    testImplementation(projects.core.testing)

    androidTestImplementation(projects.core.testing)
}