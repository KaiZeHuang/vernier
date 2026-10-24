plugins {
    id("vernier.jvm.library")
}

dependencies {
    api(project(":capability:attestation:domain"))
    api(project(":core:evidence"))
    api(project(":core:report"))
    api(project(":feature:tee:domain"))
}
