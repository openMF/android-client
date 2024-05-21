plugins {
    alias(libs.plugins.mifos.android.feature)
    alias(libs.plugins.mifos.android.library.compose)
}

android {
    namespace = "com.mifos.feature.checker_inbox_task"
}

dependencies {

    implementation(project(":core:network"))
    implementation(project(":core:datastore"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    // swipe refresh
    implementation(libs.accompanist.swiperefresh)

    // coil
    implementation(libs.coil.kt.compose)

    //rxjava dependencies
    implementation(libs.rxandroid)
    implementation(libs.rxjava)

    testImplementation(libs.junit4)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}