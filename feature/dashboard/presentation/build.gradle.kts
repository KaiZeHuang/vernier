plugins {
    id("vernier.jvm.library")
}

dependencies {
    api(project(":core:evidence"))
    api(project(":core:report"))
    api(project(":core:scan"))
}
