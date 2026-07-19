plugins {
    alias(libs.plugins.library.common)
    alias(libs.plugins.library.compose)
}

android {
    namespace = "com.jigar.core.provider"
}

dependencies {
    // Feature
    implementation(project(":feature:crash"))
}
