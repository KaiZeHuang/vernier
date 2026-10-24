pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
// Local build hosts a JDK 21 toolchain already; the foojay resolver is only needed on CI to
// auto-download a toolchain, and it is not resolvable from the local plugin repositories.
// id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
// An uninitialized submodule is an empty directory: the version query below would then count this
// repository's commits, and includeBuild would fail without saying why.
if (!file("uihelper/settings.gradle.kts").isFile) {
    throw GradleException("The uihelper submodule is not checked out. Run: git submodule update --init --recursive")
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
    versionCatalogs {
        create("libs") {
            // Gradle already imports gradle/libs.versions.toml for the default catalog.
            // Match the checked-out submodule's Maven publication version automatically.
            val uihelperVersion = providers.exec {
                workingDir = file("uihelper")
                commandLine("git", "rev-list", "--count", "HEAD")
            }.standardOutput.asText.map { it.trim() }
            library("uihelper", "io.github.xiaotong6666", "uihelper")
                .version(uihelperVersion.get())
        }
    }
}

rootProject.name = "vernier"

// A module is a directory with a build file at its group's depth, and its Gradle path mirrors that
// directory, so adding a detector, capability or core module never needs an edit here.
fun includeModules(group: String, depth: Int) {
    var directories = listOf(rootDir.resolve(group))
    repeat(depth) {
        directories = directories.flatMap { directory ->
            directory.listFiles().orEmpty().filter { it.isDirectory }.sortedBy { it.name }
        }
    }
    directories
        .filter { it.resolve("build.gradle.kts").isFile }
        .forEach { include(":" + it.relativeTo(rootDir).invariantSeparatorsPath.replace('/', ':')) }
}

include(":app")
includeModules("sdk", depth = 1)
includeModules("feature", depth = 2)
includeModules("capability", depth = 2)
includeModules("core", depth = 1)

// Keep the UI framework as a Git submodule and an independent Gradle build. Its own version
// catalog is authoritative; including it as a regular project would merge Vernier's catalog into it.
includeBuild("uihelper")
