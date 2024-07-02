package textsummarizer.routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import textsummarizer.models.dto.MobileQueryDto
import textsummarizer.services.ChatGptService
import textsummarizer.services.DeviceService
import java.util.*

private val chatGptService = ChatGptService()
private val deviceService = DeviceService()
private val logger = LoggerFactory.getLogger("QueryRoutes")

fun Route.queryRoutes() {
    route("/queries") {
        post {
            val mobileQueryDto = call.receive<MobileQueryDto>()
            logger.info("Received $mobileQueryDto")

            val deviceId = call.request.headers["deviceId"]
                ?.let { UUID.fromString(it) }
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing deviceId header")
            deviceService.exists(deviceId)

            runBlocking {
                call.respond(chatGptService.query(mobileQueryDto, deviceId))
            }
        }

        route("/{id}") {
            get {
                call.parameters["id"]?.toIntOrNull()
                    ?.let {
                        logger.info("Searching for query with id $it")
                        call.respond(chatGptService.getQuery(it) ?: call.respond(HttpStatusCode.NotFound))
                    } ?: call.respond(HttpStatusCode.BadRequest, "Invalid id")
            }
        }
    }
}