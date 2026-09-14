package pl.dev.bkwiatkowski.controller.mobile.events.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.controller.mobile.events.dto.response.SessionParticipantResponseDto
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.core.security.token.USER_ID_CLAIM
import pl.dev.bkwiatkowski.domain.usecase.GetFinishedSessionParticipantsUC

class MobileGetSessionParticipantHandler(
  private val getFinishedSessionParticipantsUC: GetFinishedSessionParticipantsUC,
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

    getFinishedSessionParticipantsUC(
      params = GetFinishedSessionParticipantsUC.Params(
        sessionUuid = sessionUuid,
        userId = userId,
      ),
    ).fold(
      onRight = { participants ->
        val dto = participants.map { p ->
          SessionParticipantResponseDto(
            sessionUuid = p.sessionUuid,
            joinedAt = p.joinedAt,
            finishedAt = p.finishedAt!!,
          )
        }

        call.respond(message = dto)
      },
      onLeft = {
        call.respond(
          status = HttpStatusCode.NotFound,
          message = ErrorResponse(
            businessCode = "GET_FINISHED_PARTICIPANTS_FAILED",
            message = "Failed to retrieve finished participants"
          ),
        )
      }
    )
  }
}
