package textsummarizer.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import org.koin.ktor.ext.inject
import textsummarizer.services.JwtService

fun Application.configureSecurity() {
    val jwtService: JwtService by inject()

    install(Authentication) {
        jwt {
            realm = jwtService.jwtRealm
            verifier(jwtService.verifier)
            validate { credential ->
                if (credential.payload.audience.contains(jwtService.jwtAudience)) JWTPrincipal(credential.payload) else null
            }
        }
    }
}
