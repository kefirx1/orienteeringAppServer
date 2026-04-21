package pl.dev.bkwiatkowski.controller.settings

import io.ktor.server.auth.*
import io.ktor.server.routing.*
import pl.dev.bkwiatkowski.controller.settings.handler.SettingsHandler
import pl.dev.bkwiatkowski.core.routing.Controller

class SettingsController(
  private val settingsHandler: SettingsHandler,
) : Controller {

  override fun Route.registerRoutes() {
    authenticate {
      route("/settings") {
        get {
          settingsHandler.handle(call)
        }
      }
    }
  }
}

