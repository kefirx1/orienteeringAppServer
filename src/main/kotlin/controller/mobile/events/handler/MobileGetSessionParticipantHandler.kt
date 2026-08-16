package pl.dev.bkwiatkowski.controller.mobile.events.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.controller.mobile.events.dto.response.SessionParticipantResponseDto
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.core.security.token.USER_ID_CLAIM
import pl.dev.bkwiatkowski.domain.usecase.GetSessionParticipantUC

class MobileGetSessionParticipantHandler(
  private val getSessionParticipantUC: GetSessionParticipantUC,
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

    getSessionParticipantUC(
      params = GetSessionParticipantUC.Params(
        sessionUuid = sessionUuid,
        userId = userId,
      )
    ).fold(
      onRight = { participant ->
        val finishedAt = participant.finishedAt
        if (finishedAt == null) {
          call.respond(
            status = HttpStatusCode.BadRequest,
            message = ErrorResponse(
              businessCode = "PARTICIPANT_NOT_FINISHED",
              message = "Participant has not finished the session"
            )
          )
          return@fold
        }

        call.respond(
          message = SessionParticipantResponseDto(
            sessionUuid = participant.sessionUuid,
            joinedAt = participant.joinedAt,
            finishedAt = finishedAt,
          )
        )
      },
      onLeft = {
        call.respond(
          status = HttpStatusCode.NotFound,
          message = ErrorResponse(
            businessCode = "GET_PARTICIPANT_FAILED",
            message = "Failed to retrieve participant"
          )
        )
      }
    )
  }
}
