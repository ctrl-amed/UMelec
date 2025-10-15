// Top-level build file

buildscript {
    repositories {
        google()       // 🔹 REQUIRED
        mavenCentral()
    }
    dependencies {
        classpath("com.google.gms:google-services:4.3.15") // 🔹 REQUIRED for Google Services plugin
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}
