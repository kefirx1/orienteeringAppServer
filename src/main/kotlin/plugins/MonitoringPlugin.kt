package pl.dev.bkwiatkowski.plugins

import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.request.*
import org.slf4j.event.*
import io.ktor.http.*

private const val MAX_LOG_LENGTH = 300
private val SENSITIVE_HEADERS = setOf(
  "authorization",
  "cookie",
  "set-cookie",
  "proxy-authorization",
  "x-api-key"
)

private fun truncateBody(body: String): String {
  return if (body.length > MAX_LOG_LENGTH) {
    body.substring(0, MAX_LOG_LENGTH) + "... [truncated, total length: ${body.length}]"
  } else {
    body
  }
}

private fun formatHeaders(headers: Headers): String {
  return headers.entries().joinToString(prefix = "[", postfix = "]") { (name, values) ->
    val lower = name.lowercase()
    val displayValue = if (SENSITIVE_HEADERS.contains(lower)) {
      values.joinToString(",") { "[REDACTED]" }
    } else {
      val joined = values.joinToString(",")
      if (joined.length > 200) joined.substring(0, 200) + "..." else joined
    }
    "${name}=${displayValue}"
  }
}

val ResponseBodyLoggingPlugin = createApplicationPlugin(name = "ResponseBodyLoggingPlugin") {

  fun PipelineCall.getMessage(body: String): String =
    "Response [${this.request.httpMethod.value} ${this.request.path()}]" +
        " headers=${formatHeaders(this.request.headers)}" +
        " -> status=${this.response.status()}" +
        " body=${truncateBody(body)}"

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