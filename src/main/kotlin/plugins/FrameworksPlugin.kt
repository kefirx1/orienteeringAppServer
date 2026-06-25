package pl.dev.bkwiatkowski.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import pl.dev.bkwiatkowski.core.serialization.LocalDateSerializer
import pl.dev.bkwiatkowski.core.serialization.LocalDateTimeSerializer
import pl.dev.bkwiatkowski.di.appModule

class FrameworksPlugin {
  fun configure(application: Application) {
    application.install(plugin = ContentNegotiation) {
      json(
        Json {
          serializersModule = SerializersModule {
            contextual(LocalDateTimeSerializer)
            contextual(LocalDateSerializer)
          }
          isLenient = true
          ignoreUnknownKeys = true
        }
      )
    }
    application.install(plugin = Koin) {
      slf4jLogger()
      modules(
        appModule(application.environment.config),
      )
    }
  }
}
