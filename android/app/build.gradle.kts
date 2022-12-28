plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
}

android {
    namespace = "pro.stuermer.mandelbrot"
    compileSdk = 33

    defaultConfig {
        // API 26 | 8.0 
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0.0"
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.2"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlinx.serialization.ExperimentalSerializationApi"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    // Logging
    implementation("com.jakewharton.timber:timber:5.0.1")

    // dependency injection
    implementation("io.insert-koin:koin-android:3.3.0")
    implementation("io.insert-koin:koin-androidx-compose:3.3.0")
    implementation("io.insert-koin:koin-androidx-workmanager:3.3.0")
    testImplementation("io.insert-koin:koin-test:3.2.2")
    testImplementation("io.insert-koin:koin-test-junit4:3.2.2")


    // Compose
    implementation("androidx.activity:activity-compose:1.6.1")
    // https://developer.android.com/jetpack/androidx/releases/compose#2022.12.00
    implementation(platform("androidx.compose:compose-bom:2022.12.00"))
    // Material Design 3
    implementation("androidx.compose.material3:material3")
    // Android Studio Preview support
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    // UI Tests
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")


    // Image loading
    implementation("io.coil-kt:coil-compose:2.2.2")

    implementation("com.pngencoder:pngencoder:0.11.0")
}
