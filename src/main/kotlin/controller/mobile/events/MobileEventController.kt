package pl.dev.bkwiatkowski.controller.mobile.events

import io.ktor.server.auth.*
import io.ktor.server.routing.*
import pl.dev.bkwiatkowski.controller.mobile.events.handler.*
import pl.dev.bkwiatkowski.core.routing.Controller

class MobileEventController(
  private val eventListHandler: MobileEventListHandler,
  private val eventDetailHandler: MobileEventDetailHandler,
  private val getLastEventHandler: MobileGetLastEventHandler,
  private val joinSessionHandler: MobileJoinSessionHandler,
  private val checkSessionJoinHandler: MobileCheckSessionJoinHandler,
  private val getSessionWaypointDetailsHandler: MobileGetSessionWaypointDetailsHandler,
  private val getSessionParticipantHandler: MobileGetSessionParticipantHandler,
  private val uploadImageHandler: MobileUploadImageHandler,
  private val waypointVisitListHandler: MobileWaypointVisitListHandler,
  private val recordWaypointVisitHandler: MobileRecordWaypointVisitHandler,
  private val finishSessionHandler: MobileFinishSessionHandler,
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

        get("last") {
          getLastEventHandler.handle(call)
        }

        post("sessions/join") {
          joinSessionHandler.handle(call)
        }

        get("sessions/{sessionUuid}/joined") {
          checkSessionJoinHandler.handle(call)
        }

        get("sessions/{sessionUuid}/waypoint-details") {
          getSessionWaypointDetailsHandler.handle(call)
        }

        get("sessions/{sessionUuid}/finished-participants") {
          getSessionParticipantHandler.handle(call)
        }

        post("sessions/{sessionUuid}/upload-image") {
          uploadImageHandler.handle(call)
        }

        post("sessions/{sessionUuid}/waypoint-visits") {
          waypointVisitListHandler.handle(call)
        }

        post("sessions/{sessionUuid}/waypoint-visit") {
          recordWaypointVisitHandler.handle(call)
        }

        post("sessions/{sessionUuid}/finish") {
          finishSessionHandler.handle(call)
        }
      }
    }
  }
}