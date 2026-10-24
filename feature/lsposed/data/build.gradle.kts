plugins {
    id("vernier.android.library")
}

android {
    namespace = "com.kaizehuang.vernier.features.lsposed.data"
}

dependencies {
    implementation(project(":capability:packageinventory:data"))
    api(project(":capability:packageinventory:domain"))
    api(project(":capability:selinuxpolicy:data"))
    api(project(":core:detector"))
    api(project(":core:native"))
    implementation(project(":core:platform"))
    api(project(":feature:lsposed:domain"))
    implementation(libs.kotlinx.coroutines.android)
}
