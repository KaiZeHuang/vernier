plugins {
    id("vernier.android.library")
}

android {
    namespace = "com.kaizehuang.vernier.features.playintegrityfix.detector"
}

kotlin {
    explicitApi()
}

dependencies {
    api(project(":core:detector"))
    implementation(project(":core:evidence"))
    api(project(":core:report"))
    implementation(project(":feature:playintegrityfix:data"))
    api(project(":feature:playintegrityfix:domain"))
    api(project(":feature:playintegrityfix:presentation"))
}
