// Top-level build file where you can add configuration options common to all sub-projects/modules.

apply(from = "${rootDir}/scripts/read-arguments.gradle")

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.google.ksp) apply false
    alias(libs.plugins.google.hilt) apply false
}