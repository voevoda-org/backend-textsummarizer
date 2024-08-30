package textsummarizer.routes

import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.application.log
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import io.mockk.mockk
import textsummarizer.models.dto.ChatGPTQueryResponse
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
        val url = "$baseUrl/queries"
        val randomDeviceId= UUID.randomUUID().toString()
        val response = client.post(url) {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $accessToken")
            header("deviceId", randomDeviceId)
        }

        expect(HttpStatusCode.BadRequest) { response.status }
        expect("Device with id: $randomDeviceId is not registered.") { response.bodyAsText() }
    }


    // Doesn't work :(
    fun `Queries should return a result for happyPath`() = testApplication {
        environment {
            config = MapApplicationConfig()
        }
        externalServices {
            hosts("https://api.openai.com/v1/chat/completions") {
                routing {
                    post {
                        log.info("------------------------------------------------------")
                        call.respond(mockk<ChatGPTQueryResponse>())
                    }
                }
            }
        }
        val url = "$baseUrl/queries"
//        val response = client.config {
//            install(ContentNegotiation) {
//                json()
//            }
//        }
//        val response = httpClient().post(url) {
        val response = client.post(url){
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $accessToken")
            header("deviceId", defaultDeviceId)
            //setBody(encodeToJsonElement(MobileQueryDto.serializer("Ceci est un test. Veuillez l'ignorer.", QueryType.SUMMARIZE))
            //setBody(encodeToJsonElement(MobileQueryDto.serializer(), MobileQueryDto("Ceci est un test. Veuillez l'ignorer.", QueryType.SUMMARIZE)))
            //setBody(MobileQueryDto("Ceci est un test. Veuillez l'ignorer.", QueryType.SUMMARIZE))
        }

        expect(HttpStatusCode.OK) { response.status }
        expect("This is a test. Please ignore.") { response.bodyAsText() }
    }

    @Test
    fun testGetQueriesId() = testApplication {
        client.get("/queries/{id}").apply {
            TODO("Please write your test here")
        }
    }
}