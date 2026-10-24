plugins {
    id("vernier.jvm.library")
}

dependencies {
    implementation(project(":capability:packageinventory:domain"))
    api(project(":core:evidence"))
    api(project(":core:report"))
    api(project(":feature:virtualization:domain"))
}
