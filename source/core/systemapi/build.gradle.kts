plugins {
    alias(libs.plugins.library.common)
}

android {
    namespace = "com.jigar.core.systemapi"

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
}

dependencies {
    implementation(libs.androidx.appcompat)

    // Core
    implementation(project(":core:common"))
}