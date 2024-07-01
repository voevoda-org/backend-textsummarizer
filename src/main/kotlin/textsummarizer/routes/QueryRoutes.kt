package textsummarizer.routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import textsummarizer.models.dto.MobileQueryDto
import textsummarizer.services.ChatGptService
import java.util.*

private val chatGptService = ChatGptService()

fun Route.queriesRoute() {
    route("/queries") {
        post {
            val mobileQueryDto = call.receive<MobileQueryDto>()
            val deviceId = call.request.headers["deviceId"]?.let { UUID.fromString(it) }
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing deviceId")

            runBlocking {
                call.respond(chatGptService.query(mobileQueryDto, deviceId))
            }
        }

        route("/{id}") {
            get {
                call.parameters["id"]?.toIntOrNull()
                    ?.let {
                        call.respond(chatGptService.getQuery(it) ?: call.respond(HttpStatusCode.NotFound))
                    } ?: call.respond(HttpStatusCode.BadRequest, "Invalid id")
            }
        }
    }
}