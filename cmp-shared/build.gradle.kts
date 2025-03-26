plugins {
    alias(libs.plugins.mifos.kmp.library)
//    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.mifos.kmp.koin)
}

android {
    namespace = "com.mifos.cmp.shared"
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(projects.cmpNavigation)
        }
    }
}
