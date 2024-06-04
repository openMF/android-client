plugins {
    alias(libs.plugins.mifos.android.library)
    alias(libs.plugins.mifos.android.library.jacoco)
    alias(libs.plugins.mifos.android.hilt)
}

android {
    namespace = "com.mifos.core.datastore"

    defaultConfig {
        consumerProguardFiles("consumer-proguard-rules.pro")
    }
    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    api(projects.core.model)
    api(projects.core.common)

    api(libs.converter.gson)

    // fineract sdk dependencies
    api(libs.mifos.android.sdk.arch)

    // sdk client
    api(libs.fineract.client)
}