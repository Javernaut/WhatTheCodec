plugins {
    alias(libs.plugins.whatthecodec.jvm.library)
    // TODO add Detekt and its setup via a convention plugin
}

dependencies {
    implementation(libs.coroutines.core)
    implementation(libs.androidx.annotation)
}
