plugins {
    id("vernier.android.library")
}

android {
    namespace = "com.kaizehuang.vernier.features.virtualization.data"
}

dependencies {
    api(project(":capability:earlypreload:data"))
    api(project(":capability:helperprocess:data"))
    api(project(":capability:packageinventory:data"))
    api(project(":capability:packageinventory:domain"))
    api(project(":core:detector"))
    implementation(project(":core:native"))
    implementation(project(":core:platform"))
    api(project(":feature:virtualization:domain"))
    implementation(libs.kotlinx.coroutines.android)
}
