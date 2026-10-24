plugins {
    id("vernier.jvm.library")
}

dependencies {
    api(project(":capability:packageinventory:domain"))
    api(project(":core:evidence"))
}
