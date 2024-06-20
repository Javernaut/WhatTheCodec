package com.javernaut.whatthecodec.buildlogic.extension

import org.gradle.kotlin.dsl.DependencyHandlerScope

internal fun DependencyHandlerScope.implementation(dependency: Any) =
    dependencies.add("implementation", dependency)

internal fun DependencyHandlerScope.testImplementation(dependency: Any) =
    dependencies.add("testImplementation", dependency)

internal fun DependencyHandlerScope.androidTestImplementation(dependency: Any) =
    dependencies.add("androidTestImplementation", dependency)

internal fun DependencyHandlerScope.debugImplementation(dependency: Any) =
    dependencies.add("debugImplementation", dependency)

internal fun DependencyHandlerScope.ksp(dependency: Any) =
    dependencies.add("ksp", dependency)
