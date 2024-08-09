plugins {
    alias(libs.plugins.mifos.android.feature)
    alias(libs.plugins.mifos.android.library.compose)
    alias(libs.plugins.mifos.android.library.jacoco)
}


android {
    namespace = "com.mifos.feature.groups"
}

dependencies {
    implementation(projects.core.domain)

    // swipe refresh
    implementation(libs.accompanist.swiperefresh)

    // paging 3
    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.androidx.paging.compose)

    //DBFlow dependencies
    implementation(libs.dbflow)

    androidTestImplementation(libs.androidx.compose.ui.test)
    debugApi(libs.androidx.compose.ui.testManifest)

    testImplementation(libs.hilt.android.testing)
    testImplementation(projects.core.testing)

    androidTestImplementation(projects.core.testing)
}