package pl.dev.bkwiatkowski.controller.events.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.controller.events.dto.request.SetEventSessionJoinableRequest
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.core.security.token.USER_ID_CLAIM
import pl.dev.bkwiatkowski.domain.model.AdminPanelUser
import pl.dev.bkwiatkowski.domain.usecase.CloseEventSessionUC
import pl.dev.bkwiatkowski.domain.usecase.GetAdminPanelUserByIdUC
import pl.dev.bkwiatkowski.domain.usecase.GetEventByIdUC
import pl.dev.bkwiatkowski.domain.usecase.SetEventSessionJoinableUC

class SetEventSessionJoinableHandler(
  private val setEventSessionJoinableUC: SetEventSessionJoinableUC,
  private val getEventByIdUC: GetEventByIdUC,
  private val getAdminPanelUserByIdUC: GetAdminPanelUserByIdUC,
) {
  suspend fun handle(call: ApplicationCall) {
    val principal = call.principal<JWTPrincipal>()
    val userId = principal?.payload?.getClaim(USER_ID_CLAIM)?.asString()?.toIntOrNull()

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

    val user = getAdminPanelUserByIdUC(
      params = GetAdminPanelUserByIdUC.Params(id = userId)
    ).getRightOrElse {
      call.respond(
        status = HttpStatusCode.Forbidden,
        message = ErrorResponse(
          businessCode = "FORBIDDEN",
          message = "User does not exist"
        )
      )
      return
    }

    val eventId = call.parameters["id"]?.toIntOrNull()
    if (eventId == null) {
      call.respond(
        status = HttpStatusCode.BadRequest,
        message = ErrorResponse(
          businessCode = "INVALID_ID",
          message = "Invalid event ID"
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

    val isAdmin = user.role == AdminPanelUser.Role.ADMIN
    val isEventOwner = event.userId == userId

    if (!isAdmin && !isEventOwner) {
      call.respond(
        status = HttpStatusCode.Forbidden,
        message = ErrorResponse(
          businessCode = "ACCESS_FORBIDDEN",
          message = "You do not have permission to change session settings for this event"
        )
      )
      return
    }

    val request = call.receive<SetEventSessionJoinableRequest>()

    setEventSessionJoinableUC(params = SetEventSessionJoinableUC.Params(eventId = eventId, userCanJoin = request.userCanJoin)).fold(
      onRight = {
        call.respond(HttpStatusCode.OK)
      },
      onLeft = {
        call.respond(
          status = HttpStatusCode.InternalServerError,
          message = ErrorResponse(
            businessCode = "SESSION_UPDATE_ERROR",
            message = "Failed to update session"
          )
        )
      }
    )
  }
}
