plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.hilt)
    id("kotlin-kapt")
}

android {
    namespace = "com.vibe.hub.core.network"
    compileSdk = 36

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    api(libs.retrofit)
    api(libs.retrofit.converter.gson)
    api(libs.okhttp)
    implementation(libs.okhttp.logging)
    
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
}
