plugins {
    id("vernier.android.library")
    id("vernier.public-api")
}

android {
    namespace = "com.kaizehuang.vernier.core.detector"

    // The mockable android.jar throws from every constructor by default, so tests could not
    // create the Context a detector receives.
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

kotlin {
    explicitApi()
}

dependencies {
    api(project(":core:evidence"))
    api(project(":core:report"))
    api(libs.kotlinx.coroutines.core)
}
