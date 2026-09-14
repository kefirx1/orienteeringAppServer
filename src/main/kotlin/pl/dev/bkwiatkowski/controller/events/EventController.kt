package pl.dev.bkwiatkowski.controller.events

import io.ktor.server.auth.*
import io.ktor.server.routing.*
import pl.dev.bkwiatkowski.controller.events.handler.*
import pl.dev.bkwiatkowski.core.routing.Controller

class EventController(
  private val eventListHandler: EventListHandler,
  private val eventDetailHandler: EventDetailHandler,
  private val addEventHandler: AddEventHandler,
  private val deleteEventHandler: DeleteEventHandler,
  private val eventParticipantsProgressionHandler: EventParticipantsProgressionHandler,
  private val eventGetUserSessionWaypointDetailsHandler: GetUserSessionWaypointDetailsHandler,
  private val createEventSessionHandler: CreateEventSessionHandler,
  private val setEventSessionJoinableHandler: SetEventSessionJoinableHandler,
  private val closeEventSessionHandler: CloseEventSessionHandler,
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

        get("{id}/session/{sessionUuid}/user/{userId}/waypoints") {
          eventGetUserSessionWaypointDetailsHandler.handle(call)
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

        get("images") {
          imageHandler.handle(call)
        }

        delete("{id}") {
          deleteEventHandler.handle(call)
        }
      }
    }
  }
}
