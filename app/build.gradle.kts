import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.compose.compiler)
    alias(libs.plugins.detekt)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.google.hilt)
}

android {
    namespace = "com.javernaut.whatthecodec"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
        targetSdk = 34

        versionCode = 4100
        versionName = "4.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"))
        }
        debug {
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    buildFeatures {
        compose = true
    }
    bundle {
        language {
            enableSplit = true
        }
        density {
            enableSplit = true
        }
        abi {
            enableSplit = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    signingConfigs {
        create("google") {
            val alias = rootProject.ext["signing.google.alias"] as? String
            if (!alias.isNullOrEmpty()) {
                keyAlias = rootProject.ext["signing.google.alias"] as String
                keyPassword = rootProject.ext["signing.google.password"] as String
                storeFile = file(rootProject.ext["signing.google.storeFile"] as String)
                storePassword = rootProject.ext["signing.google.storePassword"] as String
            }
        }
        create("amazon") {
            val alias = rootProject.ext["signing.amazon.alias"] as? String
            if (!alias.isNullOrEmpty()) {
                keyAlias = rootProject.ext["signing.amazon.alias"] as String
                keyPassword = rootProject.ext["signing.amazon.password"] as String
                storeFile = file(rootProject.ext["signing.amazon.storeFile"] as String)
                storePassword = rootProject.ext["signing.amazon.storePassword"] as String
            }
        }
        create("huawei") {
            val alias = rootProject.ext["signing.huawei.alias"] as? String
            if (!alias.isNullOrEmpty()) {
                keyAlias = rootProject.ext["signing.huawei.alias"] as String
                keyPassword = rootProject.ext["signing.huawei.password"] as String
                storeFile = file(rootProject.ext["signing.huawei.storeFile"] as String)
                storePassword = rootProject.ext["signing.huawei.storePassword"] as String
            }
        }
    }
    flavorDimensions += "market"
    productFlavors {
        create("google") {
            dimension = "market"
            ndk {
                // Since the App Bundle is used, there is no problem in packaging all these ABIs
                abiFilters += listOf("x86", "x86_64", "armeabi-v7a", "arm64-v8a")
            }
            signingConfig = signingConfigs.getByName("google")
        }
        create("amazon") {
            dimension = "market"
            applicationIdSuffix = ".amzn"
            ndk {
                // Amazon Appstore doesn't support multiple APKs for non-Amazon devices.
                // There is no point in x86 support here, as the majority of devices with the
                // Amazon Appstore are ARM-based. And it seems to be a common practice for other
                // apps in this market.
                abiFilters += listOf("armeabi-v7a", "arm64-v8a")
            }
            signingConfig = signingConfigs.getByName("amazon")
        }
        create("huawei") {
            dimension = "market"
            applicationIdSuffix = ".huawei"
            ndk {
                // Huawei App Gallery supports App Bundle format
                abiFilters += listOf("x86_64", "arm64-v8a")
            }
            signingConfig = signingConfigs.getByName("huawei")
        }
    }
    lint {
        disable += "ExtraTranslation"
    }
    packaging {
        resources.excludes.add("META-INF/LICENSE*.md")
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
        freeCompilerArgs.add("-Xcontext-receivers")
    }
}

detekt {
    config.setFrom("$projectDir/../config/detekt/detekt.yml")
    buildUponDefaultConfig = true
}

hilt {
    enableAggregatingTask = true
}

dependencies {
    implementation(project(":features:settings:api"))
    implementation(project(":features:settings:impl"))
    ksp(libs.dagger.compiler)
    ksp(libs.dagger.hilt.compiler)
    implementation(libs.dagger.hilt.android)

    implementation(libs.bundles.kotlinx.coroutines)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.androidx.compose)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.bundles.androidx.lifecycle)
    implementation(libs.androidx.palette)
    implementation(libs.androidx.savedstate)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.window)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.mediafile)

    testImplementation(libs.mockk.unit)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.androidx.test.uiautomator)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.ext.truth)
    androidTestImplementation(libs.mockk.instrumentation)
    androidTestImplementation(libs.screengrab)
}
