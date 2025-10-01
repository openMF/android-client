@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("mifos.cmp.feature")
}

android {
    namespace = "com.mifos.androidclient.features.shares"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.json) 
            implementation(libs.jb.lifecycleViewmodel)
            api(projects.core.model) 
            api(projects.core.common) 
        }
        
        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
        }
    }
}
dependencies {
    implementation(projects.cmpShared)
    implementation(projects.core.ui)
    implementation(projects.core.domain)
    implementation(projects.core.designsystem)
    implementation(projects.core.datastore)
    implementation(projects.core.network)
    implementation(projects.core.database)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.jb.lifecycleViewmodel)
    implementation(libs.jb.lifecycleruntime)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.recyclerview)
    implementation(libs.google.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.espresso.core)
}