plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.billsplit"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.billsplit"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.glide)  // pin to 4.x, remember the compileSdk 37 issue from Glide 5.x
    implementation(libs.security.crypto)  // for EncryptedSharedPreferences (JWT storage)
    implementation(libs.material.v1120)  // Chips, same pattern as ThoughtBook's emotion pills
    implementation(libs.core.splashscreen)
    implementation("androidx.core:core-splashscreen:1.2.0")
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.viewpager2)

}