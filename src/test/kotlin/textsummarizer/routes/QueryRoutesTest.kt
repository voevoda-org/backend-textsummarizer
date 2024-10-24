package textsummarizer.routes

import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import textsummarizer.models.dto.ChatGPTQueryResponseDto
import textsummarizer.models.dto.ChatGPTRequestFromMobileDto
import textsummarizer.models.dto.ChatGPTResponseChoiceDto
import textsummarizer.models.dto.ChatGPTResponseMessageDto
import textsummarizer.models.dto.ChatGPTResponseUsageDto
import textsummarizer.models.dto.QueryType
import textsummarizer.module
import textsummarizer.services.DeviceService
import textsummarizer.services.JwtService
import textsummarizer.util.baseUrl
import textsummarizer.util.defaultDeviceId
import java.util.*
import kotlin.test.Test
import kotlin.test.expect

class QueryRoutesTest {

    private val accessToken = JwtService(DeviceService()).createAccessToken()

    @Test
    fun `Queries should return Bad Request when no deviceId header is present`() = testApplication {
        application {
            module()
        }
        val url = "$baseUrl/queries"
        val response = client.post(url) {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $accessToken")
        }

        expect(HttpStatusCode.BadRequest) { response.status }
        expect("Missing deviceId header.") { response.bodyAsText() }
    }

    @Test
    fun `Queries should return Bad Request when deviceId does not exist in database`() = testApplication {
        application {
            module()
        }
        val url = "$baseUrl/queries"
        val randomDeviceId = UUID.randomUUID().toString()
        val response = client.post(url) {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $accessToken")
            header("deviceId", randomDeviceId)
        }

        expect(HttpStatusCode.BadRequest) { response.status }
        expect("Device with id: $randomDeviceId is not registered.") { response.bodyAsText() }
    }


    // Doesn't work :(
    // Most likely it's the openApiClient.post that's causing the issue
    fun `Queries should return a result for happyPath`() = testApplication {
        application {
            module()
        }
        environment {
            config = MapApplicationConfig()
        }
        externalServices {
            hosts("https://api.openai.com") {
                this@hosts.install(io.ktor.server.plugins.contentnegotiation.ContentNegotiation) {
                    json()
                }
                routing {
                    post("/v1/chat/completions") {
                        call.respond(
                            ChatGPTQueryResponseDto(
                                id = "chatcmpl-ALuWQEmY63gHyd1iBbZkweFUVpc9v",
                                obj = "chat.completion",
                                created = 1729785522,
                                model = "gpt-3.5-turbo-0125",
                                choices = listOf(
                                    ChatGPTResponseChoiceDto(
                                        index = 0,
                                        chatGPTResponseMessageDto = ChatGPTResponseMessageDto(
                                            role = "assistant",
                                            content = "{\n    \"title\": \"Overview of the United States of America\",\n    \"summary\": \"The USA is a powerful and diverse country located in North America, known for its strong economy, democratic system of government, diverse population, and promotion of individual freedoms. It has the largest economy globally, driven by industries like technology and finance, and is a leader in innovation and research. The country plays a crucial role in international affairs, shaping global events and policies. Overall, the USA's history, culture, and values have a significant impact on both its society and the world.\"\n}"
                                        ),
                                        logProbs = null,
                                        finishReason = "stop"
                                    )
                                ),
                                chatGPTResponseUsageDto = ChatGPTResponseUsageDto(
                                    promptTokens = 322,
                                    completionTokens = 116,
                                    totalTokens = 438
                                ),
                                systemFingerprint = null
                            )
                        )
                    }
                }
            }
        }
        val url = "$baseUrl/queries"
        val response = createClient {
            install(ContentNegotiation) {
                json()
            }
        }.post(url) {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $accessToken")
            header("deviceId", defaultDeviceId)
            setBody(ChatGPTRequestFromMobileDto("Ceci est un test. Veuillez l'ignorer.", QueryType.SUMMARIZE))
        }

        expect(HttpStatusCode.OK) { response.status }
        expect("This is a test. Please ignore.") { response.bodyAsText() }
    }
}