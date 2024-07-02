package textsummarizer.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import textsummarizer.routes.devicesRoute
import textsummarizer.routes.loginRoutes
import textsummarizer.routes.queriesRoute
import textsummarizer.routes.metricRoutes

fun Application.configureRouting() {
    routing {
        metricRoutes()

        route("/api/v1") {
            route("/test") {
                get {
                    call.respond("Hello World!")
                }
            }
            authenticate {
                devicesRoute()
                queriesRoute()
            }
            loginRoutes()
        }
    }
}
