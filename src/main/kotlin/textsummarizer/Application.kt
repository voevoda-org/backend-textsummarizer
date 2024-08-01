package textsummarizer

import io.ktor.server.application.Application
import textsummarizer.plugins.DatabaseFactory
import textsummarizer.plugins.configureHTTP
import textsummarizer.plugins.configureMonitoring
import textsummarizer.plugins.configureRouting
import textsummarizer.plugins.configureSecurity
import textsummarizer.plugins.configureSerialization
import textsummarizer.services.ChatGptService
import textsummarizer.services.DeviceService
import textsummarizer.services.JwtService

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val deviceService = DeviceService()
    val jwtService = JwtService(deviceService)
    val chatGptService = ChatGptService()

    configureMonitoring()
    configureSerialization()
    configureHTTP()
    configureSecurity(jwtService)

    DatabaseFactory.db

    configureRouting(jwtService, deviceService, chatGptService)
}
