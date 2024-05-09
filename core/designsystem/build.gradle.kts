plugins {
    alias(libs.plugins.mifos.android.library.compose)
    alias(libs.plugins.mifos.android.library)
}

android {
    namespace = "com.mifos.core.designsystem"
}

dependencies {

    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.foundation.layout)
    api(libs.androidx.compose.material.iconsExtended)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.runtime)
    api(libs.androidx.compose.ui.util)
    api(libs.androidx.activity.compose)

    // coil
    implementation(libs.coil.kt.compose)
}