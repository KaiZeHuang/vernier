plugins {
    id("vernier.android.library")
}

android {
    namespace = "com.kaizehuang.vernier.capability.earlypreload.data"
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(project(":core:native"))
}
