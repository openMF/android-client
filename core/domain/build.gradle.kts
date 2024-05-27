plugins {
    alias(libs.plugins.mifos.android.library)
    alias(libs.plugins.mifos.android.library.jacoco)
    alias(libs.plugins.mifos.android.hilt)
}

android {
    namespace = "com.mifos.core.domain"
}

dependencies {
    api(projects.core.data)
    api(projects.core.model)

    implementation(libs.javax.inject)

    implementation(libs.dbflow)

    // sdk client
//    implementation(libs.fineract.client)

    implementation(libs.rxandroid)
    implementation(libs.rxjava)

    implementation(libs.okhttp)

    implementation(libs.androidx.paging.runtime.ktx)

    testImplementation(projects.core.testing)
}