plugins {
    alias(libs.plugins.mifos.android.feature)
    alias(libs.plugins.mifos.android.library.compose)
}

android {
    namespace = "com.mifos.feature.collection_sheet"
}

dependencies {
    androidTestImplementation(projects.core.testing)
}