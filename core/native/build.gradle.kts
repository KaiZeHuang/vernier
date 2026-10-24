plugins {
    id("vernier.jvm.library")
}

kotlin {
    explicitApi()
}

dependencies {
    api(project(":core:evidence"))
}
