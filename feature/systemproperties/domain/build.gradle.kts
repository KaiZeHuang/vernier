plugins {
    id("vernier.jvm.library")
}

dependencies {
    api(project(":capability:systemproperties:domain"))
    api(project(":core:evidence"))
}
