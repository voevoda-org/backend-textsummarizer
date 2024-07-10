package textsummarizer.utils

import com.typesafe.config.ConfigFactory
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpResponseValidator
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
    val openApiClient: HttpClient = HttpClient(CIO) {
        // TODO
//        HttpResponseValidator {
//            validateResponse { response ->
//                val error: Error = response.body()
//                if (error.code != 0) {
//                    throw CustomResponseException(response, "Code: ${error.code}, message: ${error.message}")
//                }
//            }
//        }
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