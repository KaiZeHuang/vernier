// A separate build that consumes the published SDK AAR exactly as another application would.
// Publish it first: ./gradlew :sdk:aar:publish, then ./gradlew -p samples/sdk-consumer assembleDebug
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven(url = file("../../sdk/aar/build/repository"))
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
    versionCatalogs {
        create("libs") {
            from(files("../../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "vernier-sdk-consumer"
