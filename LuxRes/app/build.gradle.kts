plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.luxres"
    compileSdk = 35

    defaultConfig {
        // ... other defaultConfig settings ...

        javaCompileOptions {
            annotationProcessorOptions {
                // Use Kotlin map syntax here
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas"
                    // Add other arguments here if needed, separated by commas
                    // "otherArgument" to "value"
                )
                // Alternatively, if this is the only argument:
                // arguments = mapOf("room.schemaLocation" to "$projectDir/schemas")
            }
        }
        applicationId = "com.example.luxres"
        minSdk = 25
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("com.google.android.material:material:1.12.0")
    // Retrofit
    //noinspection UseTomlInstead
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    // Converter for JSON
    //noinspection UseTomlInstead
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    // OkHttp for logging (optional but useful for debugging)
    //noinspection UseTomlInstead
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.3")

    //noinspection UseTomlInstead,GradleDependency
    implementation ("com.google.android.material:material:1.11.0") // or latest

    //noinspection GradleDependency,UseTomlInstead
    implementation ("androidx.compose.material3:material3:1.1.2")

    val room_version = "2.6.1" // Define variable using val (Kotlin syntax)

    implementation("androidx.room:room-runtime:$room_version")
    // For annotation processing in Kotlin script, use 'ksp' or 'kapt' depending on your setup
    // If using KSP (recommended):
    // ksp("androidx.room:room-compiler:$room_version")
    // If using KAPT:
    annotationProcessor("androidx.room:room-compiler:$room_version") // Keep this if using annotationProcessor

    implementation("androidx.biometric:biometric:1.2.0-alpha05")

    // Image Loading Library - Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    // Use latest stable version
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0") // Or 'kapt'/'ksp' if using Kotlin plugins


}