plugins {
    alias(libs.plugins.mifos.android.feature)
    alias(libs.plugins.mifos.android.library.compose)
    alias(libs.plugins.mifos.android.library.jacoco)
}

android {
    namespace = "com.mifos.feature.client"
}

dependencies {

    implementation(projects.core.data)
    implementation(projects.core.domain)
    implementation(projects.core.datastore)

    // swipe refresh
    implementation(libs.accompanist.permission)
    implementation(libs.accompanist.swiperefresh)

    implementation(libs.coil.kt.compose)
    implementation(libs.androidx.paging.compose)

    testImplementation(libs.hilt.android.testing)
    testImplementation(projects.core.testing)

    androidTestImplementation(projects.core.testing)

    implementation(libs.androidx.material)
}