package textsummarizer.utils

import com.typesafe.config.ConfigFactory
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.config.HoconApplicationConfig
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import textsummarizer.exceptions.ChatGPTApiAuthorizationException
import textsummarizer.exceptions.ChatGPTGenericErrorException

private val logger = LoggerFactory.getLogger("ChatGPTHttpClient")

object ChatGPTHttpClient {
    private val config = HoconApplicationConfig(ConfigFactory.load())

    private val chatGptAuthKey = config.property("chatGpt.authKey").getString()
    val openApiClient: HttpClient = HttpClient(CIO) {
        HttpResponseValidator {
            validateResponse { response ->
                logger.debug(response.toString())
                when(response.status){
                    HttpStatusCode.Unauthorized -> throw ChatGPTApiAuthorizationException(response.status)
                    else -> throw ChatGPTGenericErrorException(response.status)
                }
            }
        }
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = 5)
            exponentialDelay()
        }
        install(HttpSend) {
            maxSendCount = 50
        }
        
        defaultRequest {
            header(HttpHeaders.Authorization, "Bearer $chatGptAuthKey")
            header("Content-Type","application/json")
        }
    }
}