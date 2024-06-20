plugins {
    alias(libs.plugins.whatthecodec.android.library)
    // TODO add Detekt and its setup via a convention plugin
}

android {
    namespace = "com.javernaut.whatthecodec.feature.home.localization"

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
    implementation(libs.mediafile)
}
