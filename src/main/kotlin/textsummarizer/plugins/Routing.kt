package textsummarizer.plugins

import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import textsummarizer.routes.loginRoutes
import textsummarizer.routes.queryRoutes
import textsummarizer.services.ChatGptService
import textsummarizer.services.DeviceService
import textsummarizer.services.JwtService

fun Application.configureRouting(jwtService: JwtService, deviceService: DeviceService, chatGptService: ChatGptService) {
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
            route("/auth") {
                loginRoutes(jwtService, deviceService)
            }
        }
    }
}
