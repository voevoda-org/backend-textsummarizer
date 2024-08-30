package textsummarizer.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import org.slf4j.LoggerFactory
import textsummarizer.services.DeviceService
import java.util.*

private val logger = LoggerFactory.getLogger("SubscriptionRoutes")

fun Route.subscriptionRoutes(deviceService: DeviceService) {
    route("/subscription") {
        get {
            val deviceId = call.request.headers["deviceId"]
                ?.let { UUID.fromString(it) }
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing deviceId header.")
            if (!deviceService.exists(deviceId)) {
                call.respond(HttpStatusCode.BadRequest, "Device with id: $deviceId is not registered.")
            }

            logger.info("Retrieving subscription for device with id: $deviceId")

            deviceService.findById(deviceId)?.subscriptionId
                ?.let {
                    logger.info("Retrieved subscription for device with deviceId: $deviceId and subscriptionId: $it")
                    call.respond(HttpStatusCode.OK, it) }
                ?: call.respond(HttpStatusCode.NotFound, "Subscription not found for device with id: $deviceId.")
        }
    }
}