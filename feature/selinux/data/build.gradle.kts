plugins {
    id("vernier.android.library")
}

android {
    namespace = "com.kaizehuang.vernier.features.selinux.data"
}

dependencies {
    api(project(":capability:selinuxpolicy:data"))
    api(project(":core:detector"))
    api(project(":core:native"))
    implementation(project(":core:platform"))
    api(project(":feature:selinux:domain"))
    implementation(libs.kotlinx.coroutines.android)
}
