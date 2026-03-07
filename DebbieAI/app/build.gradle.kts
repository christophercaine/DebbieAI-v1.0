plugins {
    id("com.android.application")
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    // Add the Kotlin Serialization plugin
    kotlin("plugin.serialization") version "1.9.0" // Use your Kotlin version
    id("com.google.gms.google-services")
}

android {
    namespace = "com.debbiedoesit.antigravity"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.debbiedoesit.antigravity"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a"))
        }

        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] = "$projectDir/schemas"
                arguments["room.incrementalAnnotationProcessing"] = "true"
                arguments["room.schemaLocation.disable"] = "true"
            }
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    buildTypes {
        debug {
            isDebuggable = true
        }
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "google/protobuf/*.proto"
            pickFirsts += "META-INF/INDEX.LIST"
            pickFirsts += "META-INF/DEPENDENCIES"
        }
    }

    configurations.all {
        exclude(group = "com.google.protobuf", module = "protobuf-java")
        resolutionStrategy.eachDependency {
            if (requested.group == "com.google.protobuf" && requested.name == "protobuf-java") {
                useTarget("com.google.protobuf:protobuf-javalite:3.21.12")
            }
        }
    }
}

// Kotlin toolchain is handled by AGP 9.0 built-in support

dependencies {
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    
    // Simplified networking
    implementation(libs.okhttp)
    implementation(libs.gson)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)

    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Coil for image loading
    implementation(libs.coil.compose)

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    
    // Accompanist Permissions
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")

    // Google Photos Library API
    implementation("com.google.photos.library:google-photos-library-client:1.7.0")
    
    // Google Auth & Sign-In
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.android.gms:play-services-base:18.2.0")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.11.0")
    
    // Google API Client
    implementation("com.google.api-client:google-api-client:1.35.2")
    implementation("com.google.api-client:google-api-client-android:1.35.2")
    implementation("com.google.http-client:google-http-client-android:1.42.3")

    // Antigravity v2.0 Core AI & Media
    implementation(libs.mediapipeGenai)
    implementation(libs.mediapipeVision)
    implementation(libs.ffmpegKit)
    implementation(libs.mlkitObject)
    implementation(libs.mlkitText)
    implementation(libs.arcore)
    implementation(libs.itext7)
    implementation(libs.androidxDatastore)
    implementation(libs.sceneviewAr)

    // ML Kit Coroutine support (for .await())
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // TensorFlow Lite (for YOLODetector)
    implementation("org.tensorflow:tensorflow-lite:2.16.1")
    implementation("org.tensorflow:tensorflow-lite-gpu:2.16.1")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

