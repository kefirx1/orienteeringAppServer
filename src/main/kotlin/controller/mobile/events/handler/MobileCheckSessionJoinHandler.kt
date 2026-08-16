package pl.dev.bkwiatkowski.controller.mobile.events.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.controller.mobile.events.dto.response.IsUserInSessionResponseDto
import pl.dev.bkwiatkowski.controller.mobile.events.dto.response.JoinStatus
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.core.security.token.USER_ID_CLAIM
import pl.dev.bkwiatkowski.domain.usecase.GetSessionParticipantUC
import pl.dev.bkwiatkowski.domain.usecase.IsUserInSessionUC

class MobileCheckSessionJoinHandler(
  private val isUserInSessionUC: IsUserInSessionUC,
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

    isUserInSessionUC(
      params = IsUserInSessionUC.Params(
        sessionUuid = sessionUuid,
        userId = userId,
      )
    ).fold(
      onRight = { isInSession ->
        if (isInSession) {
          call.respond(message = IsUserInSessionResponseDto(status = JoinStatus.JOINED))
        } else {
          val participant = getSessionParticipantUC(
            params = GetSessionParticipantUC.Params(
              sessionUuid = sessionUuid,
              userId = userId,
            )
          ).getRightOrNull()

          val status = when {
            participant == null -> JoinStatus.NOT_JOINED
            participant.finishedAt != null -> JoinStatus.FINISHED
            else -> JoinStatus.NOT_JOINED
          }
          call.respond(message = IsUserInSessionResponseDto(status = status))
        }
      },
      onLeft = {
        call.respond(
          status = HttpStatusCode.BadRequest,
          message = ErrorResponse(
            businessCode = "CHECK_SESSION_FAILED",
            message = "Failed to check session membership",
          )
        )
      }
    )
  }
}
