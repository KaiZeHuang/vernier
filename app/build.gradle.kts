import com.kaizehuang.vernier.buildlogic.detectorModules
import com.kaizehuang.vernier.buildlogic.generateDetectorCards

plugins {
    id("vernier.android.application")
    id("vernier.android.apk-artifacts")
}

android {
    namespace = "com.kaizehuang.vernier"

    defaultConfig {
        applicationId = "com.kaizehuang.vernier"
    }
}

// Every detector's dashboard card, discovered like the ui modules the application depends on.
generateDetectorCards(packageName = "com.kaizehuang.vernier.ui")

dependencies {
    implementation(libs.uihelper)
    implementation(libs.miuix.ui)
    implementation(libs.miuix.nav)
    implementation(project(":core:navigation"))
    implementation(project(":core:detector"))
    implementation(project(":core:evidence"))
    implementation(project(":core:scan"))
    implementation(project(":core:ui"))
    implementation(project(":core:designsystem"))
    implementation(project(":feature:dashboard:presentation"))
    implementation(project(":feature:dashboard:ui"))
    implementation(project(":feature:deviceinfo:data"))
    implementation(project(":feature:deviceinfo:domain"))
    implementation(project(":feature:deviceinfo:ui"))
    implementation(project(":feature:settings:presentation"))
    implementation(project(":feature:settings:ui"))
    implementation(project(":feature:update:data"))
    implementation(project(":feature:update:domain"))
    implementation(project(":feature:update:presentation"))
    implementation(project(":feature:update:ui"))
    implementation(project(":sdk:runtime"))
    // Every detector's dashboard card, discovered like the SDK discovers the detectors.
    detectorModules("ui").forEach { implementation(project(it)) }
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.material)
    testImplementation(project(":core:report"))
    testImplementation(project(":feature:deviceinfo:presentation"))
    testImplementation(libs.bundles.test.unit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.ui.tooling)
}
