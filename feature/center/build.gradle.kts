plugins {
    alias(libs.plugins.mifos.android.feature)
    alias(libs.plugins.mifos.android.library.compose)
    alias(libs.plugins.mifos.android.library.jacoco)
}

android {
    namespace = "com.mifos.feature.center"
}

dependencies {

    implementation(projects.core.datastore)
    implementation(projects.core.network)
    implementation(projects.core.domain)

    implementation(libs.androidx.material)

    //DBFlow dependencies
    kapt(libs.dbflow.processor)
    implementation(libs.dbflow)
    kapt(libs.github.dbflow.processor)
    testImplementation(libs.hilt.android.testing)
    testImplementation(projects.core.testing)

    androidTestImplementation(projects.core.testing)

    //paging compose
    implementation(libs.androidx.paging.compose)

    //coil
    implementation(libs.coil.kt.compose)
}