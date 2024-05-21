plugins {
    alias(libs.plugins.mifos.android.feature)
    alias(libs.plugins.mifos.android.library.compose)
}

android {
    namespace = "com.mifos.feature.client"
}

dependencies {

    implementation(project(":core:datastore"))
    implementation(project(":core:network"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    // swipe refresh
    implementation(libs.accompanist.swiperefresh)
    implementation(libs.accompanist.permission)

    // coil
    implementation(libs.coil.kt.compose)

    // fineract sdk dependencies
    implementation(libs.mifos.android.sdk.arch)

    // sdk client
    implementation(libs.fineract.client)

    //DBFlow dependencies
    ksp(libs.dbflow.processor)
    implementation(libs.dbflow)
    ksp(libs.github.dbflow.processor)

    //rxjava dependencies
    implementation(libs.rxandroid)
    implementation(libs.rxjava)

    // paging 3
    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.androidx.paging.compose)

    testImplementation(libs.junit4)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)

}