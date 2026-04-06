package pl.dev.bkwiatkowski.plugins

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import pl.dev.bkwiatkowski.di.appModule

class FrameworksPlugin {
  fun configure(application: Application) {
    application.install(plugin = ContentNegotiation) {
      json()
    }
    application.install(plugin = Koin) {
      slf4jLogger()
      modules(
        appModule(application.environment.config),
      )
    }
  }
}
