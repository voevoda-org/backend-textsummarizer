package textsummarizer

import io.ktor.server.application.*
import textsummarizer.plugins.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureMonitoring()
    //configureHTTP()
    configureSecurity()

    DatabaseFactory.db

    configureRouting()
}
