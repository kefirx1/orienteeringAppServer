package pl.dev.bkwiatkowski.controller.events.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.controller.events.dto.response.SessionWaypointDetailWebDto
import pl.dev.bkwiatkowski.controller.events.dto.response.SessionWaypointDetailsWebResponseDto
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.core.security.token.USER_ID_CLAIM
import pl.dev.bkwiatkowski.core.security.token.USER_ROLE_CLAIM
import pl.dev.bkwiatkowski.domain.model.AdminPanelUser
import pl.dev.bkwiatkowski.domain.usecase.GetEventByIdUC
import pl.dev.bkwiatkowski.domain.usecase.GetUserSessionWaypointDetailsUC

class GetUserSessionWaypointDetailsHandler(
  private val getUserSessionWaypointDetailsUC: GetUserSessionWaypointDetailsUC,
  private val getEventByIdUC: GetEventByIdUC,
) {
  suspend fun handle(call: ApplicationCall) {
    val principal = call.principal<JWTPrincipal>()
    val userId = principal?.payload?.getClaim(USER_ID_CLAIM)?.asString()?.toIntOrNull()
    val userRole = principal?.payload?.getClaim(USER_ROLE_CLAIM)?.asString()

    if (userId == null) {
      call.respond(
        status = HttpStatusCode.Unauthorized,
        message = ErrorResponse(
          businessCode = "UNAUTHORIZED",
          message = "User is not authenticated"
        )
      )
      return
    }

    if (userRole == null) {
      call.respond(
        status = HttpStatusCode.Forbidden,
        message = ErrorResponse(
          businessCode = "FORBIDDEN",
          message = "User role not available"
        )
      )
      return
    }

    val eventId = call.parameters["id"]?.toIntOrNull() ?: run {
      call.respond(
        status = HttpStatusCode.BadRequest,
        message = ErrorResponse(
          businessCode = "INVALID_REQUEST",
          message = "Missing or invalid eventId parameter"
        )
      )
      return
    }

    val event = getEventByIdUC(params = GetEventByIdUC.Params(eventId = eventId)).getRightOrElse {
      call.respond(
        status = HttpStatusCode.NotFound,
        message = ErrorResponse(
          businessCode = "EVENT_NOT_FOUND",
          message = "Event not found"
        )
      )
      return
    }

    val isAdmin = userRole == AdminPanelUser.Role.ADMIN.name
    val isEventOwner = event.userId == userId

    if (!isAdmin && !isEventOwner) {
      call.respond(
        status = HttpStatusCode.Forbidden,
        message = ErrorResponse(
          businessCode = "ACCESS_FORBIDDEN",
          message = "You do not have permission to view this event's session waypoint details"
        )
      )
      return
    }

    val sessionUuid = call.parameters["sessionUuid"]
    if (sessionUuid.isNullOrBlank()) {
      call.respond(
        status = HttpStatusCode.BadRequest,
        message = ErrorResponse(
          businessCode = "INVALID_REQUEST",
          message = "Missing or invalid sessionUuid path parameter"
        )
      )
      return
    }

    val mobileUserId = call.parameters["userId"]?.toIntOrNull() ?: run {
      call.respond(
        status = HttpStatusCode.BadRequest,
        message = ErrorResponse(
          businessCode = "INVALID_REQUEST",
          message = "Missing or invalid userId path parameter"
        )
      )
      return
    }

    getUserSessionWaypointDetailsUC(
      params = GetUserSessionWaypointDetailsUC.Params(
        sessionUuid = sessionUuid,
        userId = mobileUserId,
      )
    ).fold(
      onRight = { details ->
        val dto = details.map { detail ->
          SessionWaypointDetailWebDto(
            waypointId = detail.waypointId,
            visitedAt = detail.visitedAt,
            label = detail.label,
            imagePath = detail.imagePath,
          )
        }

        call.respond(
          status = HttpStatusCode.OK,
          message = SessionWaypointDetailsWebResponseDto(sessionWaypointDetails = dto),
        )
      },
      onLeft = {
        call.respond(
          status = HttpStatusCode.InternalServerError,
          message = ErrorResponse(
            businessCode = "FETCH_SESSION_WAYPOINT_DETAILS_FAILED",
            message = "Failed to fetch session waypoint details",
          )
        )
      }
    )
  }
}
