package pl.dev.bkwiatkowski.controller.mobile.events

import io.ktor.server.auth.*
import io.ktor.server.routing.*
import pl.dev.bkwiatkowski.controller.mobile.events.handler.MobileEventDetailHandler
import pl.dev.bkwiatkowski.controller.mobile.events.handler.MobileEventListHandler
import pl.dev.bkwiatkowski.controller.mobile.events.handler.MobileJoinSessionHandler
import pl.dev.bkwiatkowski.controller.mobile.events.handler.MobileCheckSessionJoinHandler
import pl.dev.bkwiatkowski.core.routing.Controller

class MobileEventController(
  private val eventListHandler: MobileEventListHandler,
  private val eventDetailHandler: MobileEventDetailHandler,
  private val joinSessionHandler: MobileJoinSessionHandler,
  private val checkSessionJoinHandler: MobileCheckSessionJoinHandler,
) : Controller {

  override fun Route.registerRoutes() {
    authenticate {
      route("/mobile/events") {
        get {
          eventListHandler.handle(call)
        }

        get("{id}") {
          eventDetailHandler.handle(call)
        }

        post("sessions/join") {
          joinSessionHandler.handle(call)
        }

        get("sessions/{sessionUuid}/joined") {
          checkSessionJoinHandler.handle(call)
        }
      }
    }
  }
}