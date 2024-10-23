package textsummarizer.plugins.di

import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.ktor.plugin.Koin
import org.slf4j.LoggerFactory

fun Application.configureDi() {
    install(Koin) {
        LoggerFactory.getLogger("Koin")
        modules(appModule)
    }
}