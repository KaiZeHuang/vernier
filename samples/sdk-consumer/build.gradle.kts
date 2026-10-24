plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.kaizehuang.vernier.sample"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.kaizehuang.vernier.sample"
        minSdk = 29
        targetSdk = 37
    }
}

dependencies {
    implementation("com.kaizehuang.vernier:vernier-sdk:0.0.0-SNAPSHOT")
}
