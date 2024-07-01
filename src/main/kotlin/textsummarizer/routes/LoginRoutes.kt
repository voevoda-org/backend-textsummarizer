package textsummarizer.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import java.util.*

fun Route.loginRoutes(){
    post("/login") {
        val jwtAudience = environment.config.property("jwt.audience").getString()
        val jwtDomain = environment.config.property("jwt.domain").getString()
        val jwtSecret = environment.config.property("jwt.secret").getString()

        val mobileSecret = call.receiveText()
        if (mobileSecret != environment.config.property("mobile.password").getString()) {
            return@post call.respond(
                HttpStatusCode.BadRequest
            )
        }

        val token = JWT.create()
            .withAudience(jwtAudience)
            .withIssuer(jwtDomain)
            .withExpiresAt(Date(System.currentTimeMillis() + (10 * 60000))) // 10 minutes
            .sign(Algorithm.HMAC256(jwtSecret))
        call.respond(hashMapOf("token" to token))
    }
}