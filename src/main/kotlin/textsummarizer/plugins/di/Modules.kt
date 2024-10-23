package textsummarizer.plugins.di

import org.koin.dsl.module
import textsummarizer.services.ChatGptService
import textsummarizer.services.DeviceService
import textsummarizer.services.JwtService

val appModule = module {
    single { DeviceService() }
    single { JwtService(get()) }
    single { ChatGptService() }
}