pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

// Local build hosts a JDK 21 toolchain already; the foojay resolver is only needed on CI to
// auto-download a toolchain, and it is not resolvable from the local plugin repositories.
// id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "build-logic"
