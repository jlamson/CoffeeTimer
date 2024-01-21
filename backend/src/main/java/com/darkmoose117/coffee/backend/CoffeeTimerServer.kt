package com.darkmoose117.coffee.backend

import com.darkmoose117.coffee.data.PourOver
import com.darkmoose117.coffee.data.Recipe
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import java.util.TimeZone

fun main() {
    System.setProperty("user.timezone", "UTC")
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))

    embeddedServer(
        factory = Netty,
        port = 8080,
        host = "0.0.0.0",
        module = Application::module,
        configure = {
            requestQueueLimit = 1024
            runningLimit = 1024
            responseWriteTimeoutSeconds = 60
        }
    ).start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }
    routing {
        get("/") {
            call.respond(mapOf("message" to "Hello from the backend!"))
        }
        get("/recipes") {
            call.respond(
                status = HttpStatusCode.OK,
                message = mapOf(
                    "recipes" to listOf(
                        PourOver.Small,
                        PourOver.Medium
                    )
                )
            )
        }
    }
}