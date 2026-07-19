plugins {
    alias(libs.plugins.library.common)
}

android {
    namespace = "com.jigar.feature.flavor.foss"
}

dependencies {
    // Core
    implementation(project(":core:provider"))
}
