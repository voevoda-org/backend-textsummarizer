package textsummarizer.routes

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import textsummarizer.models.Device
import textsummarizer.services.DeviceService
import java.util.UUID

private val service = DeviceService()

fun Route.devicesRoute() {
    route("/v1/devices/{id}") {
        post {
            val id = UUID.fromString(call.parameters["id"])

            id?.let {
                service.save(Device { this.id = id })
                    .also { call.respond(HttpStatusCode.Created, id) }
            } ?: call.respond(HttpStatusCode.BadRequest)
        }
    }
}