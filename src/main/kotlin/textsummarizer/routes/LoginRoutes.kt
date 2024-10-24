package textsummarizer.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

import org.slf4j.LoggerFactory
import textsummarizer.models.Device
import textsummarizer.models.dto.request.RefreshTokenRequest
import textsummarizer.models.dto.response.AuthResponse
import textsummarizer.models.dto.response.RefreshTokenResponse
import textsummarizer.services.DeviceService
import textsummarizer.services.JwtService
import java.time.LocalDateTime
import java.util.*

private val logger = LoggerFactory.getLogger("LoginRoutes")

fun Route.loginRoutes(jwtService: JwtService, deviceService: DeviceService) {
    //val jwtService: JwtService by inject()
    //val deviceService: DeviceService by inject()

    route("/auth") {
        post("/login") {
            val deviceId = call.request.headers["deviceId"]?.let { UUID.fromString(it) }
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing deviceId header.")

            val mobileSecret = call.receiveText()
            if (mobileSecret != environment.config.property("mobile.password").getString()) {
                logger.warn("DeviceId $deviceId attempted login with $mobileSecret.")
                return@post call.respond(
                    HttpStatusCode.Unauthorized, "Wrong password."
                )
            }

            deviceService.save(
                Device {
                    this.id = deviceId
                    this.subscriptionId = null
                    this.createdAt = LocalDateTime.now()
                }
            )

            logger.info("Device $deviceId logged in successfully.")

            call.respond(
                AuthResponse(
                    accessToken = jwtService.createAccessToken(),
                    refreshToken = jwtService.createRefreshToken(deviceId),
                )
            )
        }

        post("/refresh") {
            val deviceId = call.request.headers["deviceId"]?.let { UUID.fromString(it) }
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing deviceId header.")

            if (!deviceService.exists(deviceId)) {
                return@post call.respond(
                    HttpStatusCode.Unauthorized,
                    "Can't refresh for unregistered device with id: $deviceId."
                )
            }

            val refreshTokenRequest = call.receive<RefreshTokenRequest>()

            jwtService.refreshToken(
                refreshToken = refreshTokenRequest.refreshToken,
                deviceId = deviceId
            )
                ?.let { refreshToken -> call.respond(RefreshTokenResponse(accessToken = refreshToken)) }
                ?: call.respond(HttpStatusCode.Unauthorized)
        }
    }
}