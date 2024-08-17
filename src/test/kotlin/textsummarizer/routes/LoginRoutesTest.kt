package textsummarizer.routes

import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import textsummarizer.models.dto.request.RefreshTokenRequest
import textsummarizer.models.dto.response.AuthResponse
import textsummarizer.util.baseUrl
import textsummarizer.util.defaultDeviceId
import java.util.*
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.expect

class LoginRoutesTest {

    @Test
    fun `Login should return error if deviceId header missing`() = testApplication {
        val url = "$baseUrl/auth/login"
        val response = client.post(url) {
            setBody("test123")
        }

        expect(HttpStatusCode.BadRequest) { response.status }
        expect("Missing deviceId header.") {response.bodyAsText()}
    }

    @Test
    fun `Login should return unauthorized if password is wrong`() {
        testApplication {
            val url = "$baseUrl/auth/login"
            val response = client.post(url) {
                setBody("test1234")
                header("deviceId", defaultDeviceId)
            }

            expect(HttpStatusCode.Unauthorized) { response.status }
            expect("Wrong password.") {response.bodyAsText()}
        }
    }

    @Test
    fun `Login should access and refreshToken if password is correct`() {
        testApplication {
            val url = "$baseUrl/auth/login"
            val response = client.post(url) {
                setBody("test123")
                header("deviceId", defaultDeviceId)
            }

            expect(HttpStatusCode.OK) { response.status }
            assertTrue { response.bodyAsText().contains("accessToken") }
            assertTrue { response.bodyAsText().contains("refreshToken") }
        }
    }

    @Test
    fun `Refresh should return error if deviceId header missing`() = testApplication {
        val url = "$baseUrl/auth/refresh"
        val response = client.post(url) {
            setBody("test123")
        }

        expect(HttpStatusCode.BadRequest) { response.status }
        expect("Missing deviceId header.") {response.bodyAsText()}
    }

    @Test
    fun `Refresh should return unauthorized if device doesn't exist`() = testApplication {
        val url = "$baseUrl/auth/refresh"
        val randomUUID = UUID.randomUUID()
        val response = client.post(url) {
            header("deviceId", randomUUID)
            contentType(ContentType.Application.Json)
        }

        expect(HttpStatusCode.Unauthorized) { response.status }
        expect("Can't refresh for unregistered device with id: $randomUUID.") {response.bodyAsText()}
    }

    @Test
    fun `Refresh deviceId in refreshToken and header should match`() = testApplication {
        val url = "$baseUrl/auth"
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val refreshToken = client.post("$url/login") {
            setBody("test123")
            header("deviceId", defaultDeviceId)
        }.body<AuthResponse>()
            .refreshToken

        val response = client.post("$url/refresh") {
            header("deviceId", UUID.randomUUID().toString())
            setBody(RefreshTokenRequest(refreshToken))
            contentType(ContentType.Application.Json)
        }

        expect(HttpStatusCode.Unauthorized) { response.status }
    }

    @Test
    fun `Refresh deviceId in refreshToken should exist in db`() = testApplication {
        val url = "$baseUrl/auth"
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val refreshToken = client.post("$url/login") {
            setBody("test123")
            header("deviceId", UUID.randomUUID())
        }.body<AuthResponse>()
            .refreshToken

        val response = client.post("$url/refresh") {
            header("deviceId", defaultDeviceId)
            setBody(RefreshTokenRequest(refreshToken))
            contentType(ContentType.Application.Json)
        }

        expect(HttpStatusCode.Unauthorized) { response.status }
    }

    @Test
    fun `Refresh should return a new accessToken`() = testApplication {
        val url = "$baseUrl/auth"
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val refreshToken = client.post("$url/login") {
            setBody("test123")
            header("deviceId", defaultDeviceId)
        }.body<AuthResponse>()
            .refreshToken

        val response = client.post("$url/refresh") {
            header("deviceId", defaultDeviceId)
            setBody(RefreshTokenRequest(refreshToken))
            contentType(ContentType.Application.Json)
        }

        expect(HttpStatusCode.OK) { response.status }
        assertTrue { response.bodyAsText().contains("accessToken") }
        assertTrue { !response.bodyAsText().contains("refreshToken") }
    }
}