plugins {
    alias(libs.plugins.whatthecodec.android.library)
    alias(libs.plugins.whatthecodec.android.compose)
    alias(libs.plugins.whatthecodec.android.hilt)
    // TODO add Detekt and its setup via a convention plugin
}

android {
    namespace = "com.javernaut.whatthecodec.feature.settings.ui"

    defaultConfig {
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

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.androidx.browser)
}
