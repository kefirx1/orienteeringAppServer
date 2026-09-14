package pl.dev.bkwiatkowski.controller.mobile.settings

import io.ktor.server.auth.*
import io.ktor.server.routing.*
import pl.dev.bkwiatkowski.controller.mobile.settings.handler.MobileChangePasswordHandler
import pl.dev.bkwiatkowski.controller.mobile.settings.handler.MobileSettingsHandler
import pl.dev.bkwiatkowski.core.routing.Controller

class MobileSettingsController(
  private val mobileSettingsHandler: MobileSettingsHandler,
  private val mobileChangePasswordHandler: MobileChangePasswordHandler,
) : Controller {

  override fun Route.registerRoutes() {
    authenticate {
      route("/mobile/settings") {
        get("") {
          mobileSettingsHandler.handle(call)
        }

        post("/change-password") {
          mobileChangePasswordHandler.handle(call)
        }
      }
    }
  }
}
