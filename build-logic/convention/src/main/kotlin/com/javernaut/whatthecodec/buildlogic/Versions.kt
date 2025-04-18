package com.javernaut.whatthecodec.buildlogic

import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

object Versions {
    const val targetSdk = 34
    const val compileSdk = 35
    const val minSdk = 24

    val javaSourceCompatibility = JavaVersion.VERSION_17
    val javaTargetCompatibility = JavaVersion.VERSION_17
    val kotlinJvmTarget = JvmTarget.JVM_17
}
