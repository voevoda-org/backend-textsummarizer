package textsummarizer.services

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.typesafe.config.ConfigFactory
import io.ktor.server.config.HoconApplicationConfig
import org.slf4j.LoggerFactory
import java.util.*

private const val validityInMs = 3600000
private const val refreshValidityInMs: Int = 86_400_000

private val logger = LoggerFactory.getLogger("JwtService")

class JwtService(
    private val deviceService: DeviceService
) {
    private val config = HoconApplicationConfig(ConfigFactory.load())

    val jwtRealm = config.property("jwt.realm").getString()
    val jwtAudience = config.property("jwt.audience").getString()
    private val jwtDomain = config.property("jwt.domain").getString()
    private val jwtSecret = config.property("jwt.secret").getString()

    val verifier: JWTVerifier = JWT
        .require(Algorithm.HMAC256(jwtSecret))
        .withAudience(jwtAudience)
        .withIssuer(jwtDomain)
        .build()

    fun createAccessToken(): String = JWT.create()
        .withAudience(jwtAudience)
        .withIssuer(jwtDomain)
        .withExpiresAt(Date(System.currentTimeMillis() + validityInMs)) // 1 hour
        .sign(Algorithm.HMAC256(jwtSecret))

    fun createRefreshToken(deviceId: UUID): String = JWT.create()
        .withAudience(jwtAudience)
        .withIssuer(jwtDomain)
        .withClaim("deviceId", deviceId.toString())
        .withExpiresAt(Date(System.currentTimeMillis() + refreshValidityInMs)) // 1 day
        .sign(Algorithm.HMAC256(jwtSecret))

    suspend fun refreshToken(deviceId: UUID, refreshToken: String): String? =
        verifyRefreshToken(deviceId, refreshToken)
            ?.let { createAccessToken() }

    private suspend fun verifyRefreshToken(deviceId: UUID, token: String): DecodedJWT? {
        val decodedJwt = verifier.verify(token)
        val deviceIdFromToken = decodedJwt.getClaim("deviceId").`as`(UUID::class.java)
        if (deviceIdFromToken == deviceId) {
            if (deviceService.exists(deviceIdFromToken)) {
                return decodedJwt
            } else {
                logger.error("DeviceId $deviceIdFromToken does not exist in db.")
                return null
            }
        } else {
            logger.error("Header DeviceId $deviceId and token DeviceId $deviceIdFromToken do not match.")
            return null
        }
    }
}