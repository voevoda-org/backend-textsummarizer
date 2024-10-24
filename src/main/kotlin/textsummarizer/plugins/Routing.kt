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
import textsummarizer.services.ChatGptService
import textsummarizer.services.DeviceService
import textsummarizer.services.JwtService

fun Application.configureRouting(deviceService: DeviceService, jwtService: JwtService, chatGptService: ChatGptService) {
    routing {
        route("/api/v1") {
            route("/test") {
                get {
                    call.respond("Hello World!")
                }
            }
            authenticate {
                queryRoutes(deviceService, chatGptService)
            }
            subscriptionRoutes(deviceService)
            loginRoutes(jwtService, deviceService)
        }
    }
}
