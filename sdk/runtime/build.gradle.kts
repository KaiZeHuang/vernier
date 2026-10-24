import com.kaizehuang.vernier.buildlogic.DETECTOR_LAYER
import com.kaizehuang.vernier.buildlogic.detectorModules

plugins {
    id("vernier.android.library")
    id("vernier.public-api")
    id("vernier.contract-values")
}

android {
    namespace = "com.kaizehuang.vernier.sdk"
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(project(":capability:earlypreload:data"))
    implementation(project(":capability:packageinventory:data"))
    implementation(project(":capability:packageinventory:domain"))
    implementation(project(":capability:selinuxpolicy:data"))
    implementation(project(":core:evidence"))
    api(project(":core:detector"))
    api(project(":core:report"))
    // Every detector unit, discovered from its directory, so the catalog and the AAR can hold all of them.
    detectorModules(DETECTOR_LAYER).forEach { implementation(project(it)) }
    api(libs.kotlinx.coroutines.core)
}
