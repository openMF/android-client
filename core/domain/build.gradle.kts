plugins {
    alias(libs.plugins.mifos.android.library)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.mifos.core.domain"
}

dependencies {

    api(projects.core.data)
    api(projects.core.datastore)

    implementation(libs.javax.inject)

    // Retrofit Core
    implementation(libs.retrofit.core)

    // paging 3
    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.androidx.paging.compose)

    testImplementation(projects.core.testing)
    testImplementation (libs.androidx.paging.common.ktx)
    testImplementation (libs.androidx.paging.testing)
}