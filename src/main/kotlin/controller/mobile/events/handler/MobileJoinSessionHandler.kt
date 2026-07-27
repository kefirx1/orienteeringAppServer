package pl.dev.bkwiatkowski.controller.mobile.events.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.controller.mobile.events.dto.request.JoinSessionRequestDto
import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.core.security.token.USER_ID_CLAIM
import pl.dev.bkwiatkowski.domain.usecase.JoinSessionUC

class MobileJoinSessionHandler(
  private val joinSessionUC: JoinSessionUC,
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

    val request = either {
      call.receiveNullable<JoinSessionRequestDto>()
    }.getRightOrNull() ?: run {
      call.respond(
        status = HttpStatusCode.BadRequest,
        message = ErrorResponse(
          businessCode = "INVALID_REQUEST",
          message = "Invalid request body"
        )
      )
      return
    }

    joinSessionUC(
      params = JoinSessionUC.Params(
        sessionUuid = request.sessionUuid,
        userId = userId,
      )
    ).fold(
      onRight = {
        call.respond(message = HttpStatusCode.OK)
      },
      onLeft = { error ->
        call.respond(
          status = HttpStatusCode.BadRequest,
          message = ErrorResponse(
            businessCode = "JOIN_SESSION_FAILED",
            message = when (error) {
              is DomainError.Custom -> error.e?.message ?: "Failed to join session"
            }
          ),
        )
      }
    )
  }
}
