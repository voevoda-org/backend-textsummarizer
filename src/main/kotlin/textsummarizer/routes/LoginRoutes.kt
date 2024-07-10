package textsummarizer.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import org.slf4j.LoggerFactory
import textsummarizer.models.Device
import textsummarizer.services.DeviceService
import java.time.LocalDateTime
import java.util.*

private val deviceService = DeviceService()
private val logger = LoggerFactory.getLogger("LoginRoutes")

fun Route.loginRoutes() {
    post("/login") {
        val deviceId = call.request.headers["deviceId"]?.let { UUID.fromString(it) }
            ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing deviceId header")

        val jwtAudience = environment.config.property("jwt.audience").getString()
        val jwtDomain = environment.config.property("jwt.domain").getString()
        val jwtSecret = environment.config.property("jwt.secret").getString()

        val mobileSecret = call.receiveText()
        if (mobileSecret != environment.config.property("mobile.password").getString()) {
            logger.warn("DevicedId $deviceId attempted login with $mobileSecret.")
            return@post call.respond(
                HttpStatusCode.BadRequest, "Wrong password."
            )
        }

        deviceService.save(
            Device {
                this.id = deviceId
                this.createdAt = LocalDateTime.now()
            }
        )

        logger.info("Logged $deviceId in successfully.")

        val token = JWT.create()
            .withAudience(jwtAudience)
            .withIssuer(jwtDomain)
            .withExpiresAt(Date(System.currentTimeMillis() + (10 * 60000))) // 10 minutes
            .sign(Algorithm.HMAC256(jwtSecret))
        call.respond(hashMapOf("token" to token))
    }
}