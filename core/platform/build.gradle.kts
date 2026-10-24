plugins {
    id("vernier.android.library")
}

android {
    namespace = "com.kaizehuang.vernier.core.platform"

    // The mockable android.jar throws from every constructor by default, so tests could not create
    // the platform exceptions whose names this module pins.
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(project(":core:evidence"))
    implementation(libs.kotlinx.coroutines.core)
}
