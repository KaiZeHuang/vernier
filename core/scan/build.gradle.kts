plugins {
    id("vernier.jvm.library")
}

kotlin {
    explicitApi()
}

dependencies {
    api(project(":core:evidence"))
    api(project(":core:report"))
    api(libs.kotlinx.coroutines.core)
}
