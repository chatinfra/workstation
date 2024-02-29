plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "chatinfra"

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://androidx.dev/storage/compose-compiler/repository/")
    }
    versionCatalogs {
        create("deps") {
            from(files("deps.versions.toml"))
        }
    }
}

