plugins {
    alias(libs.plugins.library.common)
    alias(libs.plugins.library.firebase)
}

android {
    namespace = "com.jigar.feature.flavor.premium"
}

dependencies {
    // Core
    implementation(project(":core:provider"))
}
