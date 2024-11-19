// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
}
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }

    }
}
buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.7.2")
        classpath("com.google.gms:google-services:4.4.0")
    }
}