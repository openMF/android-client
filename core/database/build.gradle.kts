plugins {
    alias(libs.plugins.mifos.android.library)
    alias(libs.plugins.mifos.android.hilt)
    alias(libs.plugins.mifos.android.library.jacoco)
    id(libs.plugins.kotlin.parcelize.get().pluginId)
}

android {
    namespace = "com.mifos.core.database"

    defaultConfig {
        testInstrumentationRunner = "com.mifos.core.testing.MifosTestRunner"
    }
}

dependencies {
    api(projects.core.common)
    api(projects.core.model)
    api(projects.core.datastore)

    implementation(libs.converter.gson)

    //rxjava dependencies
    implementation(libs.rxandroid)
    implementation(libs.rxjava)

    //DBFlow dependencies
    kapt(libs.dbflow.processor)
    implementation(libs.dbflow)
    kapt(libs.github.dbflow.processor)

    // Hilt dependency
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // fineract sdk dependencies
    implementation(libs.mifos.android.sdk.arch)

    // sdk client
    implementation(libs.fineract.client)

    androidTestImplementation(projects.core.testing)
}