plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("kotlin-parcelize")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.mifos.core.network"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation(project(":core:datastore"))
    implementation(project(":core:common"))

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //rxjava dependencies
    implementation("io.reactivex:rxandroid:1.1.0")
    implementation("io.reactivex:rxjava:1.3.8")

    // Hilt dependency
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-android-compiler:2.50")

    // fineract sdk dependencies
    implementation("com.github.openMF:mifos-android-sdk-arch:1.06")

    // sdk client
    implementation("com.github.openMF:fineract-client:2.0.3")

    //DBFlow dependencies
    kapt("com.github.raizlabs.dbflow.dbflow:dbflow-processor:3.1.1")
    implementation("com.github.raizlabs.dbflow.dbflow:dbflow:3.1.1")
    kapt("com.github.raizlabs.dbflow:dbflow-processor:4.2.4")

    // Mongo Realm
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    implementation("io.realm.kotlin:library-base:1.11.0")


    //Square dependencies
    implementation("com.squareup.retrofit2:retrofit:2.9.0") {
        // exclude Retrofit’s OkHttp peer-dependency module and define your own module import
        exclude(module = "okhttp")
    }
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("com.squareup.retrofit2:adapter-rxjava:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.2")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation("com.jakewharton.fliptables:fliptables:1.0.1")

    //stetho dependencies
    implementation("com.facebook.stetho:stetho:1.3.1")
    implementation("com.facebook.stetho:stetho-okhttp3:1.3.1")

    // coil
    implementation("io.coil-kt:coil-compose:2.5.0")
}