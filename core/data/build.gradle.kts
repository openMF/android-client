plugins {
    alias(libs.plugins.mifos.android.library)
    alias(libs.plugins.kotlin.android)
    id(libs.plugins.kotlin.parcelize.get().pluginId)
    id(libs.plugins.kotlin.kapt.get().pluginId)
}

android {
    namespace = "com.mifos.core.data"

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    implementation(projects.core.datastore)
    implementation(projects.core.network)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    testImplementation(libs.junit4)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)

    //DBFlow dependencies
    kapt(libs.dbflow.processor)
    implementation(libs.dbflow)
    kapt(libs.github.dbflow.processor)

    // Hilt dependency
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    //rxjava dependencies
    implementation(libs.rxandroid)
    implementation(libs.rxjava)

    implementation(libs.okhttp)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlinx.serialization.json)
    testImplementation(projects.core.testing)
}