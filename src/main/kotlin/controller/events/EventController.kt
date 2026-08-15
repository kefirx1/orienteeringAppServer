package pl.dev.bkwiatkowski.controller.events

import io.ktor.server.auth.*
import io.ktor.server.routing.*
import pl.dev.bkwiatkowski.controller.events.handler.AddEventHandler
import pl.dev.bkwiatkowski.controller.events.handler.CompleteEventHandler
import pl.dev.bkwiatkowski.controller.events.handler.DeleteEventHandler
import pl.dev.bkwiatkowski.controller.events.handler.EventDetailHandler
import pl.dev.bkwiatkowski.controller.events.handler.EventListHandler
import pl.dev.bkwiatkowski.controller.events.handler.EventParticipantsProgressionHandler
import pl.dev.bkwiatkowski.controller.events.handler.EventImageHandler
import pl.dev.bkwiatkowski.core.routing.Controller

class EventController(
  private val eventListHandler: EventListHandler,
  private val eventDetailHandler: EventDetailHandler,
  private val addEventHandler: AddEventHandler,
  private val deleteEventHandler: DeleteEventHandler,
  private val completeEventHandler: CompleteEventHandler,
  private val eventParticipantsProgressionHandler: EventParticipantsProgressionHandler,
  private val createEventSessionHandler: pl.dev.bkwiatkowski.controller.events.handler.CreateEventSessionHandler,
  private val setEventSessionJoinableHandler: pl.dev.bkwiatkowski.controller.events.handler.SetEventSessionJoinableHandler,
  private val closeEventSessionHandler: pl.dev.bkwiatkowski.controller.events.handler.CloseEventSessionHandler,
  private val imageHandler: EventImageHandler,
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

        get("{eventId}/participants-progression") {
          eventParticipantsProgressionHandler.handle(call)
        }

        post("/complete") {
          completeEventHandler.handle(call)
        }

        post("{id}/session") {
          createEventSessionHandler.handle(call)
        }

        post("{id}/session/joinable") {
          setEventSessionJoinableHandler.handle(call)
        }

        post("{id}/session/close") {
          closeEventSessionHandler.handle(call)
        }

        get("images/{path...}") {
          imageHandler.handle(call)
        }

        delete("{id}") {
          deleteEventHandler.handle(call)
        }
      }
    }
  }
}
