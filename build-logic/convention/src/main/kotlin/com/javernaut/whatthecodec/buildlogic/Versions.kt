package com.javernaut.whatthecodec.buildlogic

import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

object Versions {
    const val targetSdk = 34
    const val compileSdk = 34
    const val minSdk = 24

    val javaSourceCompatibility = JavaVersion.VERSION_1_8
    val javaTargetCompatibility = JavaVersion.VERSION_1_8
    val kotlinJvmTarget = JvmTarget.JVM_1_8
}
