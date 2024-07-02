package textsummarizer.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import textsummarizer.models.Device
import textsummarizer.services.DeviceService
import java.time.LocalDateTime
import java.util.*

private val service = DeviceService()

fun Route.devicesRoute() {
    route("/devices") {
        post("/{id}") {
            val id = UUID.fromString(call.parameters["id"])

            id?.let {
                service.save(
                    Device {
                        this.id = id
                        this.createdAt = LocalDateTime.now()
                    }
                )
                    .also { call.respond(HttpStatusCode.Created, id.toString()) }
            } ?: call.respond(HttpStatusCode.BadRequest)
        }
    }
}