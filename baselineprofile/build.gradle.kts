import com.android.build.api.dsl.ManagedVirtualDevice

plugins {
    alias(libs.plugins.whatthecodec.android.test)
    alias(libs.plugins.androidx.baselineprofile)
}

android {
    namespace = "com.javernaut.whatthecodec.baselineprofile"

    defaultConfig {
        minSdk = 28
    }

    targetProjectPath = ":app"

    flavorDimensions += listOf("market")
    productFlavors {
        create("google") { dimension = "market" }
        create("amazon") { dimension = "market" }
        create("huawei") { dimension = "market" }
    }

    /**
     * ./gradlew :app:generateBaselineProfile \
     *      -Pandroid.testInstrumentationRunnerArguments.androidx.benchmark.enabledRules=BaselineProfile \
     *      -Pandroid.experimental.androidTest.numManagedDeviceShards=1 \
     *      -Pandroid.experimental.testOptions.managedDevices.maxConcurrentDevices=1
     *
     *      While on Github Actions, another parameter is required:
     *      -Pandroid.testoptions.manageddevices.emulator.gpu="swiftshader_indirect"
     */
    testOptions.managedDevices.devices {
        create<ManagedVirtualDevice>("pixel6Api34") {
            device = "Pixel 6"
            apiLevel = 34
            systemImageSource = "aosp"
        }
    }
}

baselineProfile {
    managedDevices += "pixel6Api34"
    useConnectedDevices = false
}

dependencies {
    implementation(libs.androidx.test.ext.junit)
    implementation(libs.androidx.test.runner)
    implementation(libs.androidx.test.uiautomator)
    implementation(libs.androidx.benchmark.macro)
}

androidComponents {
    onVariants { v ->
        val artifactsLoader = v.artifacts.getBuiltArtifactsLoader()
        v.instrumentationRunnerArguments.put(
            "targetAppId",
            v.testedApks.map { artifactsLoader.load(it)?.applicationId }
        )
    }
}
