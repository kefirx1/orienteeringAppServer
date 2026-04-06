package pl.dev.bkwiatkowski

import com.asyncapi.kotlinasyncapi.context.service.AsyncApiExtension
import com.asyncapi.kotlinasyncapi.ktor.AsyncApiPlugin
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.openapi.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*

fun Application.configureHTTP() {
  install(AsyncApiPlugin) {
    extension = AsyncApiExtension.builder {
      info {
        title("Sample API")
        version("1.0.0")
      }
    }
  }
  install(CachingHeaders) {
    options { call, outgoingContent ->
      when (outgoingContent.contentType?.withoutParameters()) {
        ContentType.Text.CSS -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 24 * 60 * 60))
        else -> null
      }
    }
  }
  install(DefaultHeaders) {
    header("X-Engine", "Ktor") // will send this header with each response
  }
  routing {
    openAPI(path = "openapi") {
      info = OpenApiInfo(title = "My API", version = "1.0.0")
    }
    swaggerUI(path = "swagger") {
      info = OpenApiInfo(title = "My API", version = "1.0.0")
    }
  }
}
