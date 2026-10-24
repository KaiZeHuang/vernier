plugins {
    id("vernier.android.library")
}

android {
    namespace = "com.kaizehuang.vernier.capability.selinuxpolicy.data"
}

kotlin {
    explicitApi()
}

dependencies {
    api(project(":core:native"))
    implementation(project(":core:platform"))
    implementation(libs.kotlinx.coroutines.android)
}
