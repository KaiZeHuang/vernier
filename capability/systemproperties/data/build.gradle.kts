plugins {
    id("vernier.android.library")
}

android {
    namespace = "com.kaizehuang.vernier.capability.systemproperties.data"
}

kotlin {
    explicitApi()
}

dependencies {
    api(project(":capability:systemproperties:domain"))
    api(project(":core:native"))
    implementation(project(":core:platform"))
}
