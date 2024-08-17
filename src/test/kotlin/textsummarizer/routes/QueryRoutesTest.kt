package textsummarizer.routes

import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.server.testing.testApplication
import textsummarizer.module
import kotlin.test.Test

class QueryRoutesTest {

    @Test
    fun testPostQueries() = testApplication {
        application {
            module()
        }
        client.post("/queries").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testGetQueriesId() = testApplication {
        application {
            module()
        }
        client.get("/queries/{id}").apply {
            TODO("Please write your test here")
        }
    }
}