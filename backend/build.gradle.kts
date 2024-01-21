plugins {
    application
    alias(libs.plugins.jvm)
}
dependencies {
    implementation(projects.shared)

    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.contentnegotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
}
application {
    mainClass.set("com.darkmoose117.coffee.backend.CoffeeTimerServerKt")
}