plugins {
    alias(libs.plugins.library.common)
}

android {
    namespace = "com.jigar.feature.flavor.alpha"
}

dependencies {
    // Core
    implementation(project(":core:provider"))
}
