plugins {
    alias(libs.plugins.whatthecodec.android.library)
    alias(libs.plugins.jetbrains.kotlin.compose)
    // TODO add Detekt and its setup via a convention plugin
}

android {
    namespace = "com.javernaut.whatthecodec.feature.settings.ui"

    buildFeatures {
        compose = true
    }

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(project(":features:settings:api"))
    implementation(project(":features:home:localization"))

    implementation(libs.bundles.androidx.lifecycle)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.androidx.compose)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.androidx.browser)
}
