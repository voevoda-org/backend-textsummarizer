package textsummarizer

import io.ktor.server.application.*
import io.ktor.server.metrics.micrometer.MicrometerMetrics
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import textsummarizer.plugins.*
import io.ktor.server.response.respond
import io.ktor.server.response.respondText

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
//    val appMicrometerRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
//    install(MicrometerMetrics) {
//        registry = appMicrometerRegistry
//    }
//    routing {
//        get("/metrics") {
//            call.respondText(appMicrometerRegistry.scrape())
//        }
//    }
    configureMonitoring()
    configureSerialization()
    configureHTTP()
    configureSecurity()

    DatabaseFactory.db

    configureRouting()
}
