plugins {
    alias(libs.plugins.mifos.android.feature)
    alias(libs.plugins.mifos.android.library.compose)
}

android {
    namespace = "com.mifos.feature.collection_sheet"
}

dependencies {

    implementation(projects.core.datastore)
    implementation(projects.core.network)

    //DBFlow dependencies
    ksp(libs.dbflow.processor)
    implementation(libs.dbflow)
    ksp(libs.github.dbflow.processor)
}