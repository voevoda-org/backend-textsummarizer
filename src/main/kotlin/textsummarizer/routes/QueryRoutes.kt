package textsummarizer.routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.slf4j.LoggerFactory
import textsummarizer.models.dto.ChatGPTRequestFromMobileDto
import textsummarizer.services.ChatGptService
import textsummarizer.services.DeviceService
import java.util.*

private val logger = LoggerFactory.getLogger("QueryRoutes")

fun Route.queryRoutes() {
    val deviceService: DeviceService by inject()
    val chatGptService: ChatGptService by inject()

    route("/queries") {
        post {
            val deviceId = call.request.headers["deviceId"]
                ?.let { UUID.fromString(it) }
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing deviceId header.")
            if (!deviceService.exists(deviceId)) {
                return@post call.respond(HttpStatusCode.BadRequest, "Device with id: $deviceId is not registered.")
            }

            val chatGPTRequestFromMobileDto = call.receive<ChatGPTRequestFromMobileDto>()
            logger.info("Received $chatGPTRequestFromMobileDto")

            call.respond(chatGptService.query(chatGPTRequestFromMobileDto, deviceId))
        }

        route("/{id}") {
            get {
                val queryId = call.parameters["id"]?.toIntOrNull()
                if (queryId != null) {
                    logger.info("Searching for query with id $queryId")

                    chatGptService.getQuery(queryId)?.let { query ->
                        call.respond(HttpStatusCode.OK, query)
                    } ?: call.respond(HttpStatusCode.NotFound)
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid id")
                }
            }
        }
    }
}