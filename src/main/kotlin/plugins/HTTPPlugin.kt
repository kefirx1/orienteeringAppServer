package pl.dev.bkwiatkowski.plugins

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
import pl.dev.bkwiatkowski.core.EnvironmentConfig

class HTTPPlugin(
  private val environmentConfig: EnvironmentConfig
) {
  fun configure(application: Application) {
    application.install(plugin = CORS) {
      environmentConfig.corsHosts.forEach { host ->
        allowHost(host)
      }
      allowMethod(HttpMethod.Options)
      allowMethod(HttpMethod.Put)
      allowMethod(HttpMethod.Delete)
      allowMethod(HttpMethod.Patch)
      allowHeader(HttpHeaders.Authorization)
      allowHeader(HttpHeaders.ContentType)
      allowHeader(HttpHeaders.AccessControlAllowOrigin)
      allowCredentials = true
    }
    application.install(AsyncApiPlugin) {
      extension = AsyncApiExtension.builder {
        info {
          title(value = "Sample API")
          version(value = "1.0.0")
        }
      }
    }
    application.install(CachingHeaders) {
      options { _, outgoingContent ->
        when (outgoingContent.contentType?.withoutParameters()) {
          ContentType.Text.CSS -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 24 * 60 * 60))
          else -> null
        }
      }
    }
    application.install(plugin = DefaultHeaders) {
      header(name = "X-Engine", value = "Ktor")
    }
    application.routing {
      swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
      openAPI(path = "openapi", swaggerFile = "openapi/documentation.yaml")
      swaggerUI(path = "swagger-mobile", swaggerFile = "openapi/mobile-documentation.yaml")
    }
  }
}