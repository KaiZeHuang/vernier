// The distributable SDK: one AAR fusing :sdk:runtime with every module it is made of.
// ./gradlew :sdk:aar:publish writes it to sdk/aar/build/repository.
plugins {
    id("vernier.sdk.distribution")
}

androidFusedLibrary {
    namespace = "com.kaizehuang.vernier.sdk.aar"
}
