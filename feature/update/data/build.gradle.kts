plugins {
    id("vernier.android.library")
}

android {
    namespace = "com.kaizehuang.vernier.features.update.data"
}

dependencies {
    api(project(":feature:update:domain"))
    implementation(libs.androidx.datastore.preferences)
    api(libs.kotlinx.coroutines.android)
}
