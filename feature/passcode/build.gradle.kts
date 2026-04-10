plugins {
    alias(libs.plugins.cmp.feature.convention)
    alias(libs.plugins.kotlin.serialization)

}

android {
    namespace = "com.mifos.feature.passcode"
}


kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.data)
            implementation(projects.coreBase.ui)
            implementation(projects.core.datastore)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.mifos.authenticator.passcode)
            implementation(libs.mifos.authenticator.biometrics)
            implementation(libs.jb.navigationevent)
        }
    }
}