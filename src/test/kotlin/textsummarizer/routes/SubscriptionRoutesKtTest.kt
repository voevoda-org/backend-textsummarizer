package textsummarizer.routes

import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import java.util.*
import kotlin.test.Test
import kotlin.test.expect

class SubscriptionRoutesKtTest {

    @Test
    fun `testGetApiV1Subscription should fail, when no deviceId is present`() = testApplication {
        client.get("/api/v1/subscription").apply {
            expect(HttpStatusCode.BadRequest) { this.status }
            expect("Missing deviceId header.") { this.bodyAsText() }
        }
    }

//    @Test
//    fun `testGetApiV1Subscription should return subscription details, when deviceId is present`(): Unit =
//        testApplication {
//            client.get("/api/v1/subscription") {
//                header("deviceId", "1234567890")
//            }.apply {
//                expect(HttpStatusCode.OK) {}
//            }
//        }

    @Test
    fun `testGetApiV1Subscription should fail, when deviceId does not exist in db`() = testApplication {
        val randomDeviceId= UUID.randomUUID().toString()
        client.get("/api/v1/subscription"){
            header("deviceId", randomDeviceId)
        }.apply {
            expect(HttpStatusCode.BadRequest) { this.status }
            expect("Device with id: $randomDeviceId is not registered.") { this.bodyAsText() }
        }
    }
}