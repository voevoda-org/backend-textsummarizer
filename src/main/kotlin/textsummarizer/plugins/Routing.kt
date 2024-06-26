package textsummarizer.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import textsummarizer.routes.devicesRoute
import textsummarizer.routes.queriesRoute

fun Application.configureRouting() {
    routing {
        route("/api") {
            get("/v1/test") {
                call.respond("Hello World!")
            }
            authenticate("auth-jwt") {
                devicesRoute()
                queriesRoute()
            }
        }
    }
}
