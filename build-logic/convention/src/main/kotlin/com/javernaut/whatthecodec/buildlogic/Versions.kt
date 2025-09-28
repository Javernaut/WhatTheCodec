package com.javernaut.whatthecodec.buildlogic

import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

object Versions {
    const val targetSdk = 36
    const val compileSdk = 36
    const val minSdk = 24

    val javaSourceCompatibility = JavaVersion.VERSION_17
    val javaTargetCompatibility = JavaVersion.VERSION_17
    val kotlinJvmTarget = JvmTarget.JVM_17
}
