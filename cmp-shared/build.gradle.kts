plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.mifos.android.koin)
}

android {
    namespace = "com.pronaycoding.cmp.shared"
}

dependencies {
    implementation(projects.cmpNavigation)
}