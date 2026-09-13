package com.javernaut.whatthecodec.buildlogic

import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

object Versions {
    const val targetSdk = 37
    const val compileSdk = 37
    const val compileSdkMinor = 2
    const val minSdk = 24

    val javaSourceCompatibility = JavaVersion.VERSION_17
    val javaTargetCompatibility = JavaVersion.VERSION_17
    val kotlinJvmTarget = JvmTarget.JVM_17
}
