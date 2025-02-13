plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlinx.serialization)
    id("kotlin-parcelize")
}

android {
    namespace = "eu.tutorials.courseapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "eu.tutorials.courseapplication"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // ComposeViewModel
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.5")
    //Network calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    //Json to kotlin mapping
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    //Image loading
    implementation ("io.coil-kt:coil-compose:2.4.0")
    //NavigationCompose
    implementation("androidx.navigation:navigation-compose:2.8.0")
    //Data serialization
    implementation(libs.kotlinx.serialization.json)
    //jwt decoder
    implementation("com.auth0.android:jwtdecode:2.0.0")
    //video
    implementation ("androidx.media3:media3-exoplayer:1.4.1")
    implementation ("androidx.compose.ui:ui:1.7.5")
    implementation ("androidx.media3:media3-ui:1.4.1")
    //Token Security
    implementation (libs.androidx.security.crypto)
    // system UI Controller
    implementation ("com.google.accompanist:accompanist-systemuicontroller:0.36.0")




    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.media3.exoplayer)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}