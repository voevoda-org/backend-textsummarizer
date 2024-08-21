package textsummarizer.util

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.ApplicationTestBuilder

const val baseUrl = "http://localhost/api/v1"

const val defaultDeviceId = "889e5d7f-cf0f-4f7d-a584-cf0587d29bbe"

fun ApplicationTestBuilder.httpClient(): HttpClient =
    createClient {
        install(ContentNegotiation) {
            json()
        }
    }