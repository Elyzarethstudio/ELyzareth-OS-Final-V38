plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.example.shared"
    compileSdk { version = release(36) { minorApiLevel = 1 } }

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    api(project(":core:foundation"))
    api(project(":core:kernel"))
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.junit)
}
