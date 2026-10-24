plugins {
    id("vernier.jvm.library")
}

dependencies {
    implementation(project(":capability:attestation:domain"))
    api(project(":core:evidence"))
    api(project(":core:report"))
    api(project(":feature:bootloader:domain"))
}
