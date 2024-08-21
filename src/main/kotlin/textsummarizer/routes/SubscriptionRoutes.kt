package textsummarizer.routes

import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("SubscriptionRoutes")

fun Route.subscriptionRoutes() {
    route("/subscription") {

    }
}