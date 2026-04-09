package pl.dev.bkwiatkowski

import com.asyncapi.kotlinasyncapi.context.service.AsyncApiExtension
import com.asyncapi.kotlinasyncapi.ktor.AsyncApiPlugin
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*

fun Application.configureHTTP() {
  install(CORS) {
    anyHost()
    allowMethod(HttpMethod.Options)
    allowMethod(HttpMethod.Put)
    allowMethod(HttpMethod.Delete)
    allowMethod(HttpMethod.Patch)
    allowHeader(HttpHeaders.Authorization)
    allowHeader(HttpHeaders.ContentType)
    allowHeader(HttpHeaders.AccessControlAllowOrigin)
  }
  install(AsyncApiPlugin) {
    extension = AsyncApiExtension.builder {
      info {
        title("Sample API")
        version("1.0.0")
      }
    }
  }
  install(CachingHeaders) {
    options { _, outgoingContent ->
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
    swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
    openAPI(path = "openapi", swaggerFile = "openapi/documentation.yaml")
  }
}
