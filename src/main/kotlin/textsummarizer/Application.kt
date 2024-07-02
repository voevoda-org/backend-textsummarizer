package textsummarizer

import io.ktor.server.application.Application
import textsummarizer.plugins.DatabaseFactory
import textsummarizer.plugins.configureHTTP
import textsummarizer.plugins.configureMonitoring
import textsummarizer.plugins.configureRouting
import textsummarizer.plugins.configureSecurity
import textsummarizer.plugins.configureSerialization

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureMonitoring()
    configureSerialization()
    configureHTTP()
    configureSecurity()

    DatabaseFactory.db

    configureRouting()
}
