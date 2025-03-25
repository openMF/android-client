plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.mifos.android.library.compose)
}

android {
    namespace = "com.mifos.cmp.navigation"
}

dependencies {
    implementation(libs.koin.android.v401)
    implementation(libs.koin.androidx.compose.v350)

    implementation(projects.feature.auth)
    implementation(projects.feature.center)
    implementation(projects.feature.checkerInboxTask)
    implementation(projects.feature.client)
    implementation(projects.feature.collectionSheet)
    implementation(projects.feature.dataTable)
    implementation(projects.feature.document)
    implementation(projects.feature.groups)
    implementation(projects.feature.loan)
    implementation(projects.feature.note)
    implementation(projects.feature.offline)
    implementation(projects.feature.pathTracking)
    implementation(projects.feature.report)
    implementation(projects.feature.savings)
    implementation(projects.feature.search)
    implementation(projects.feature.settings)
}