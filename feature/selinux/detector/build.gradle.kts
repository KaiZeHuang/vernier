plugins {
    id("vernier.android.library")
}

android {
    namespace = "com.kaizehuang.vernier.features.selinux.detector"
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(project(":capability:selinuxpolicy:data"))
    api(project(":core:detector"))
    implementation(project(":core:evidence"))
    api(project(":core:report"))
    implementation(project(":feature:selinux:data"))
    api(project(":feature:selinux:domain"))
    api(project(":feature:selinux:presentation"))
    testImplementation(project(":core:native"))
}
