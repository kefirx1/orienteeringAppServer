package pl.dev.bkwiatkowski.plugins

import io.ktor.server.application.Application
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import pl.dev.bkwiatkowski.core.routing.Controller

class RoutingPlugin(
  private val controllers: List<Controller>
) {
  fun configure(application: Application) {
    application.routing {
      route("/api") {
        controllers.forEach { controller ->
          with(controller) {
            registerRoutes()
          }
        }
      }
    }
  }
}
