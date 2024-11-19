plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.meetme"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.meetme"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    lint {
        checkReleaseBuilds = false
        warningsAsErrors = false
        disable.add("MissingTranslation")
        enable.add("deprecation")
    }
}



dependencies {
    // Core libraries
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-messaging")
//    implementation("com.google.firebase:firebase-core:17.0.0")
    implementation("com.google.firebase:firebase-database")

    // OneSignal
    //implementation("com.onesignal:OneSignal:3.10.1")

    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")

    // SwipeCards
    implementation("com.lorentzos.swipecards:library:1.0.9")

    // BottomNavigationViewEx
    implementation("com.github.ittianyu:BottomNavigationViewEx:2.0.4")

    // CircleImageView
    implementation("de.hdodenhof:circleimageview:2.2.0")

    // ShowCase card
    implementation("com.github.dimorinny:show-case-card-view:0.0.1")

    // InfiniteCards
    implementation("com.github.BakerJQ:Android-InfiniteCards:1.0.5")
}
