plugins {
    id("vernier.android.library")
}

android {
    namespace = "com.kaizehuang.vernier.capability.packageinventory.data"
}

kotlin {
    explicitApi()
}

dependencies {
    api(project(":capability:packageinventory:domain"))
    implementation(project(":core:native"))
    implementation(project(":core:platform"))
}
