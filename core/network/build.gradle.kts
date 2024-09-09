plugins {
    alias(libs.plugins.mifos.android.library)
    alias(libs.plugins.mifos.android.library.jacoco)
    alias(libs.plugins.mifos.android.hilt)
    alias(libs.plugins.secrets)
    id(libs.plugins.kotlin.parcelize.get().pluginId)
}

android {
    namespace = "com.mifos.core.network"

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    buildFeatures {
        buildConfig = true
    }
}

secrets {
    defaultPropertiesFileName = "secrets.defaults.properties"
}

dependencies {

    api(projects.core.database)
    api(projects.core.datastore)
    api(projects.core.common)

    // fineract sdk dependencies
    implementation(libs.mifos.android.sdk.arch)

    // sdk client
    implementation(libs.fineract.client)

    //DBFlow dependencies
    kapt(libs.dbflow.processor)
    implementation(libs.dbflow)

    //Square dependencies
    implementation("com.squareup.retrofit2:retrofit:2.9.0") {
        // exclude Retrofit’s OkHttp peer-dependency module and define your own module import
        exclude(module = "okhttp")
    }
    implementation(libs.converter.gson)
    implementation(libs.converter.scalars)
    implementation(libs.adapter.rxjava)
    implementation(libs.squareup.okhttp)
    implementation(libs.logging.interceptor)

    implementation(libs.fliptables)

    //stetho dependencies
    implementation(libs.stetho)
    implementation(libs.stetho.okhttp3)

    implementation(libs.coil.kt.compose)
}