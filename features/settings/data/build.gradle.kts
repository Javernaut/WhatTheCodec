plugins {
    alias(libs.plugins.whatthecodec.android.library)
    alias(libs.plugins.whatthecodec.android.hilt)
    // TODO add Detekt and its setup via a convention plugin
}

android {
    namespace = "com.javernaut.whatthecodec.feature.settings.data"

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

    implementation(libs.androidx.datastore.preferences)
}
