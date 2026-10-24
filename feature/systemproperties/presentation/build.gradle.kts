plugins {
    id("vernier.jvm.library")
}

dependencies {
    implementation(project(":capability:systemproperties:domain"))
    api(project(":core:evidence"))
    api(project(":core:report"))
    api(project(":feature:systemproperties:domain"))
}
