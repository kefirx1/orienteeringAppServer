package pl.dev.bkwiatkowski.controller.events

import io.ktor.server.auth.*
import io.ktor.server.routing.*
import pl.dev.bkwiatkowski.controller.events.handler.AddEventHandler
import pl.dev.bkwiatkowski.controller.events.handler.DeleteEventHandler
import pl.dev.bkwiatkowski.controller.events.handler.EventDetailHandler
import pl.dev.bkwiatkowski.controller.events.handler.EventListHandler
import pl.dev.bkwiatkowski.core.routing.Controller

class EventController(
  private val eventListHandler: EventListHandler,
  private val eventDetailHandler: EventDetailHandler,
  private val addEventHandler: AddEventHandler,
  private val deleteEventHandler: DeleteEventHandler,
) : Controller {

  override fun Route.registerRoutes() {
    authenticate {
      route("/events") {
        get {
          eventListHandler.handle(call)
        }

        post {
          addEventHandler.handle(call)
        }

        get("{id}") {
          eventDetailHandler.handle(call)
        }

        delete("{id}") {
          deleteEventHandler.handle(call)
        }
      }
    }
  }
}
