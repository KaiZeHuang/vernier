plugins {
    id("vernier.jvm.library")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(project(":core:evidence"))
}
