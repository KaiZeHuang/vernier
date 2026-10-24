plugins {
    id("vernier.android.library")
}

android {
    namespace = "com.kaizehuang.vernier.features.playintegrityfix.data"
}

dependencies {
    api(project(":core:detector"))
    api(project(":core:native"))
    implementation(project(":core:platform"))
    api(project(":feature:playintegrityfix:domain"))
    implementation(libs.kotlinx.coroutines.android)
}
