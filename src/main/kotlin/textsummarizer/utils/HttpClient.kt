package textsummarizer.utils

import com.typesafe.config.ConfigFactory
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.config.HoconApplicationConfig
import kotlinx.serialization.json.Json

object HttpClient {
    private val config = HoconApplicationConfig(ConfigFactory.load())

    private val chatGptAuthKey = config.property("chatGpt.authKey").getString()
    val openApiClient: HttpClient = HttpClient {
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
            //host = "api.openai.com/v1"
            header(HttpHeaders.Authorization, "Bearer $chatGptAuthKey")
            header("Content-Type","application/json")
        }
    }
}