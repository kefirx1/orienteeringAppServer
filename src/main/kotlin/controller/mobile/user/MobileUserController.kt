package pl.dev.bkwiatkowski.controller.mobile.user

import io.ktor.server.auth.*
import io.ktor.server.routing.*
import pl.dev.bkwiatkowski.controller.mobile.user.handler.MobileGetUserSessionsSummaryHandler
import pl.dev.bkwiatkowski.core.routing.Controller

class MobileUserController(
  private val getUserSessionsHandler: MobileGetUserSessionsSummaryHandler,
) : Controller {

  override fun Route.registerRoutes() {
    authenticate {
      route("/mobile/user") {
        get("{userId}/sessions") {
          getUserSessionsHandler.handle(call)
        }
      }
    }
  }
}
