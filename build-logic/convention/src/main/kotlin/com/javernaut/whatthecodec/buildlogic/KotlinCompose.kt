package com.javernaut.whatthecodec.buildlogic

import com.android.build.api.dsl.CommonExtension
import com.javernaut.whatthecodec.buildlogic.extension.androidTestImplementation
import com.javernaut.whatthecodec.buildlogic.extension.debugImplementation
import com.javernaut.whatthecodec.buildlogic.extension.implementation
import com.javernaut.whatthecodec.buildlogic.extension.libs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Configure Compose-specific options
 */
internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension,
) {
    commonExtension.apply {
        buildFeatures.apply {
            compose = true
        }

        dependencies {
            implementation(platform(libs.androidx.compose.bom))
            implementation(libs.bundles.androidx.compose)

            debugImplementation(libs.androidx.ui.test.manifest)

            androidTestImplementation(platform(libs.androidx.compose.bom))
            androidTestImplementation(libs.androidx.ui.test.junit4)
        }
    }
}
