plugins {
    id("vernier.android.library")
}

android {
    namespace = "com.kaizehuang.vernier.capability.attestation.data"
}

kotlin {
    explicitApi()
}

dependencies {
    api(project(":capability:attestation:domain"))
    implementation(project(":core:evidence"))
    implementation(project(":core:platform"))
    implementation(libs.bouncycastle.bcprov)
}
