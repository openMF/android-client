plugins {
    alias(libs.plugins.mifos.android.library)
    alias(libs.plugins.mifos.android.hilt)
    alias(libs.plugins.mifos.android.library.jacoco)
    id(libs.plugins.kotlin.parcelize.get().pluginId)
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
    api(projects.core.common)
    api(projects.core.datastore)
    api(projects.core.network)
    api(projects.core.database)

    //rxjava dependencies
    api(libs.rxandroid)
    api(libs.rxjava)

    api(libs.dbflow)
    api(libs.squareup.okhttp)

    // sdk client
    api(libs.fineract.client)

    api(libs.androidx.paging.runtime.ktx)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlinx.serialization.json)
    testImplementation(projects.core.testing)
}