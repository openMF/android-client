plugins {
    alias(libs.plugins.mifos.android.feature)
    alias(libs.plugins.mifos.android.library.compose)
}

android {
    namespace = "com.mifos.feature.groups"
}

dependencies {
    implementation(projects.core.domain)

    // swipe refresh
    implementation(libs.accompanist.swiperefresh)

    implementation(libs.hilt.android.testing)
    testImplementation(libs.hilt.android.testing)
    testImplementation(projects.core.testing)
    androidTestImplementation(projects.core.testing)

    // paging 3
    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.androidx.paging.compose)

    //DBFlow dependencies
    implementation(libs.dbflow)

    testImplementation(libs.junit4)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}