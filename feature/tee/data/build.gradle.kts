plugins {
    id("vernier.android.library")
}

android {
    namespace = "com.kaizehuang.vernier.features.tee.data"
}

dependencies {
    api(project(":capability:attestation:data"))
    api(project(":capability:attestation:domain"))
    implementation(project(":capability:systemproperties:data"))
    api(project(":core:detector"))
    api(project(":core:native"))
    implementation(project(":core:platform"))
    api(project(":feature:tee:domain"))
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.hiddenapibypass)
    api(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.annotation)
    implementation(libs.soter.core)
    testImplementation(project(":core:evidence"))
}
