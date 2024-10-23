package textsummarizer.plugins

import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import textsummarizer.routes.loginRoutes
import textsummarizer.routes.queryRoutes
import textsummarizer.routes.subscriptionRoutes

fun Application.configureRouting() {
    routing {
        route("/api/v1") {
            route("/test") {
                get {
                    call.respond("Hello World!")
                }
            }
            authenticate {
                queryRoutes()
            }
            subscriptionRoutes()
            loginRoutes()
        }
    }
}
