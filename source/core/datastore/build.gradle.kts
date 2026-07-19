plugins {
    alias(libs.plugins.library.common)
    alias(libs.plugins.library.hilt)
}

android {
    namespace = "com.jigar.core.datastore"
}

dependencies {
    // Core
    implementation(project(":core:model"))
    implementation(project(":core:common"))

    // Preferences DataStore
    implementation(libs.androidx.datastore.preferences)

    // Gson
    implementation(libs.gson)
}
