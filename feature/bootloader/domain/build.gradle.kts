plugins {
    id("vernier.jvm.library")
}

dependencies {
    api(project(":capability:attestation:domain"))
    api(project(":core:evidence"))
}
