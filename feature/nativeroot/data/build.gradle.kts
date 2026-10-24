plugins {
    id("vernier.android.library")
}

android {
    namespace = "com.kaizehuang.vernier.features.nativeroot.data"
}

dependencies {
    api(project(":capability:helperprocess:data"))
    implementation(project(":capability:packageinventory:data"))
    api(project(":capability:packageinventory:domain"))
    api(project(":core:detector"))
    api(project(":core:native"))
    implementation(project(":core:platform"))
    api(project(":feature:nativeroot:domain"))
    implementation(libs.hiddenapibypass)
    api(libs.kotlinx.coroutines.android)
}

val srcuCloseHostTest by tasks.registering(Exec::class) {
    group = "verification"
    description = "Verifies the fsnotify close sampler with injected I/O on the build host."
    commandLine("python3", layout.projectDirectory.file("src/test/native/test_srcu_close_probe.py").asFile)
}

tasks.named("unitTest") { dependsOn(srcuCloseHostTest) }
