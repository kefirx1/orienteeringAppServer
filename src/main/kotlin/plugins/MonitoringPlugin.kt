package pl.dev.bkwiatkowski.plugins

import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.request.*
import org.slf4j.event.*

val ResponseBodyLoggingPlugin = createApplicationPlugin(name = "ResponseBodyLoggingPlugin") {

  fun PipelineCall.getMessage(body: String): String =
    "Response [${this.request.httpMethod.value} ${this.request.path()}]" +
        " -> status=${this.response.status()}" +
        " body=$body"

  onCallRespond { call ->
    transformBody { data ->
      when (data) {
        is TextContent -> {
          call.application.log.info(
            call.getMessage(body = data.text)
          )
          data
        }
        is ByteArrayContent -> {
          call.application.log.info(
            call.getMessage(body = data.bytes().toString(Charsets.UTF_8))
          )
          data
        }
        else -> data
      }
    }
  }
}

class MonitoringPlugin {
  fun configure(application: Application) {
    application.install(plugin = CallLogging) {
      level = Level.INFO
      filter { call -> call.request.path().startsWith("/") }
      format { call ->
        val status = call.response.status()
        val method = call.request.httpMethod.value
        val path = call.request.path()
        "[$method] $path -> $status"
      }
    }

    application.install(ResponseBodyLoggingPlugin)
  }
}