plugins {
    id("vernier.jvm.library")
    id("vernier.public-api")
    id("vernier.contract-values")
}

kotlin {
    explicitApi()
}
