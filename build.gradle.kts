plugins {
    kotlin("jvm") version "1.9.22"
    id("application")
    alias(deps.plugins.serialization)
}

group = "ai.chatinfra"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(deps.result)
    implementation(deps.kotlinx.serialization.json)
    implementation(deps.kotlinx.coroutines.core)
    implementation(deps.kotlinx.datetime)
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}