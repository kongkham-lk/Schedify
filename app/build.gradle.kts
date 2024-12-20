plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.schedify"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.schedify"
        minSdk = 25
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.crashlytics.buildtools)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("org.jsoup:jsoup:1.13.1")
    implementation("org.seleniumhq.selenium:selenium-java:4.14.0") // Latest version as of now
    implementation("org.seleniumhq.selenium:selenium-chrome-driver:4.14.0") // For ChromeDriver
}