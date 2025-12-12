// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.gradle)
        classpath(libs.hilt.android.gradle.plugin)
    }
}
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.jetbrainsKotlinParcelize) apply false
    alias(libs.plugins.kotlinKapt) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.jetbrainsKotlinJvm) apply false
}
true // Needed to make the Suppress annotation work for the plugins block
