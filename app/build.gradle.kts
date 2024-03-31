plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.detekt)
    alias(libs.plugins.googleKsp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.javernaut.whatthecodec"
    compileSdk = 34
    defaultConfig {
        minSdk = 21
        targetSdk = 34

        versionCode = 3611
        versionName = "3.6.1"

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
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidx.compose.compiler.get()
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
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
    signingConfigs {
        create("google") {
            val alias = rootProject.ext["signing.google.alias"] as? String
            if (!alias.isNullOrEmpty()) {
                keyAlias = rootProject.ext["signing.google.alias"] as String
                keyPassword = rootProject.ext["signing.google.passowrd"] as String
                storeFile = file(rootProject.ext["signing.google.storeFile"] as String)
                storePassword = rootProject.ext["signing.google.storePassword"] as String
            }
        }
        create("amazon") {
            val alias = rootProject.ext["signing.amazon.alias"] as? String
            if (!alias.isNullOrEmpty()) {
                keyAlias = rootProject.ext["signing.amazon.alias"] as String
                keyPassword = rootProject.ext["signing.amazon.passowrd"] as String
                storeFile = file(rootProject.ext["signing.amazon.storeFile"] as String)
                storePassword = rootProject.ext["signing.amazon.storePassword"] as String
            }
        }
        create("huawei") {
            val alias = rootProject.ext["signing.huawei.alias"] as? String
            if (!alias.isNullOrEmpty()) {
                keyAlias = rootProject.ext["signing.huawei.alias"] as String
                keyPassword = rootProject.ext["signing.huawei.passowrd"] as String
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
        create("ci") {
            dimension = "market"
            ndk {
                abiFilters += "x86"
            }
        }
    }
    lint {
        disable += "ExtraTranslation"
    }
}

dependencies {
    ksp(libs.dagger.compiler)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.android)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)

    implementation(libs.mediafile)

    implementation(libs.bundles.coroutines)

    implementation(libs.material)

    implementation(libs.bundles.lifecycle)

    implementation(libs.androidx.appcompat)
    implementation(libs.palette)
    implementation(libs.savedstate)
    implementation(libs.viewpager2)
    implementation(libs.browser)
    implementation(libs.window)
    implementation(libs.androidx.navigation.compose)

    // TODO replace with own implementation
    implementation(libs.liveevent)
}

detekt {
    config.setFrom("$projectDir/../config/detekt/detekt.yml")
    buildUponDefaultConfig = true
}

hilt {
    enableAggregatingTask = true
}