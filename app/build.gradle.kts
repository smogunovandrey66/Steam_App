@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kaptAndoid)
    alias(libs.plugins.hiltAndroid)
}

android {
    namespace = "com.smogunov.steamapp"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.smogunov.steamapp"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release"){
            storeFile = file("steamapps.jks")
            storePassword = "steamapps"
            keyAlias = "steam"
            keyPassword = "steamapps"
        }
    }

//    signingConfigs {
//        release {
//            storeFile file("jksPathProj.jks")
//            storePassword "jksPassword"
//            keyAlias "alias"
//            keyPassword "jksPassword"
//        }
//    }


    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kapt {
        correctErrorTypes = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
//    packaging {
//        resources {
//            excludes += "/META-INF/{AL2.0,LGPL2.1}"
//        }
//    }
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)

    implementation(libs.com.squareup.retrofit2)
    implementation(libs.com.squareup.retrofit2.gson)
    implementation(libs.androidx.paging)
    implementation(libs.com.google.dagger)
    implementation(libs.androidx.lifecycle)
    implementation(libs.androidx.lifecyclektx)
    implementation(libs.material)
    implementation(libs.androidx.navigation)

    implementation(libs.androidx.hilt)
    implementation(libs.com.google.android.material.material)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    kapt(libs.hilt.compiler)
    
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)
    kapt(libs.room.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
}