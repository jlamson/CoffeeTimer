plugins {
    `kotlin-dsl`
//    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
}

repositories {
    gradlePluginPortal() // so that external plugins can be resolved in dependencies section
    mavenCentral()
}
