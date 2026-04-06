package pl.dev.bkwiatkowski.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.request.*
import org.slf4j.event.*

class MonitoringPlugin {
  fun configure(application: Application) {
    application.install(plugin = CallLogging) {
      level = Level.INFO
      filter { call -> call.request.path().startsWith("/") }
    }
  }
}
