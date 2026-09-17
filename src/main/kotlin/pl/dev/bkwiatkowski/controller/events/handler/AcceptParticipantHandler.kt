package pl.dev.bkwiatkowski.controller.events.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.core.security.token.USER_ID_CLAIM
import pl.dev.bkwiatkowski.domain.model.AdminPanelUser
import pl.dev.bkwiatkowski.domain.usecase.AcceptParticipantUC
import pl.dev.bkwiatkowski.domain.usecase.GetAdminPanelUserByIdUC
import pl.dev.bkwiatkowski.domain.usecase.GetEventBySessionUuidUC
import pl.dev.bkwiatkowski.domain.usecase.GetSessionParticipantByIdUC

class AcceptParticipantHandler(
  private val acceptParticipantUC: AcceptParticipantUC,
  private val getSessionParticipantByIdUC: GetSessionParticipantByIdUC,
  private val getEventBySessionUuidUC: GetEventBySessionUuidUC,
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

    val participantId = call.parameters["participantId"]?.toIntOrNull() ?: run {
      call.respond(
        status = HttpStatusCode.BadRequest,
        message = ErrorResponse(
          businessCode = "INVALID_REQUEST",
          message = "Missing or invalid participantId parameter"
        )
      )
      return
    }

    val participant = getSessionParticipantByIdUC(
      params = GetSessionParticipantByIdUC.Params(participantId = participantId)
    ).getRightOrElse {
      call.respond(
        status = HttpStatusCode.NotFound,
        message = ErrorResponse(
          businessCode = "PARTICIPANT_NOT_FOUND",
          message = "Session participant not found",
        )
      )
      return
    }

    val event = getEventBySessionUuidUC(params = GetEventBySessionUuidUC.Params(sessionUuid = participant.sessionUuid)).getRightOrElse {
      call.respond(
        status = HttpStatusCode.NotFound,
        message = ErrorResponse(
          businessCode = "EVENT_NOT_FOUND",
          message = "Event for participant session not found",
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
          message = "You do not have permission to accept this participant"
        )
      )
      return
    }

    acceptParticipantUC(params = AcceptParticipantUC.Params(participantId = participantId)).fold(
      onRight = {
        call.respond(HttpStatusCode.OK)
      },
      onLeft = {
        call.respond(
          status = HttpStatusCode.InternalServerError,
          message = ErrorResponse(
            businessCode = "ACCEPT_ERROR",
            message = "Failed to accept participant"
          )
        )
      }
    )
  }
}
