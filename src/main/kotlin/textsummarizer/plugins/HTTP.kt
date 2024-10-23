package textsummarizer.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*

fun Application.configureHTTP() {
//    install(SimpleCache) {
//        redisCache {
//            invalidateAt = 10.seconds
//            host = "localhost"
//            port = 6379
//        }
//    }
    routing {
        swaggerUI(path = "openapi")
    }
//    routing {
//        openAPI(path = "openapi")
//    }
//    routing {
//        cacheOutput(2.seconds) {
//            get("/short") {
//                call.respond(Random.nextInt().toString())
//            }
//        }
//        cacheOutput {
//            get("/default") {
//                call.respond(Random.nextInt().toString())
//            }
//        }
//    }
}
