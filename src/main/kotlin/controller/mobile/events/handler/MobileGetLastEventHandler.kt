package pl.dev.bkwiatkowski.controller.mobile.events.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.controller.mobile.events.dto.response.MobileLastEventResponseDto
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.core.security.token.USER_ID_CLAIM
import pl.dev.bkwiatkowski.domain.usecase.GetLastEventUC

class MobileGetLastEventHandler(
  private val getLastEventUC: GetLastEventUC,
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

    getLastEventUC(params = GetLastEventUC.Params).fold(
      onRight = { lastEventId ->
        call.respond(
          status = HttpStatusCode.OK,
          message = MobileLastEventResponseDto(id = lastEventId)
        )
      },
      onLeft = {
        call.respond(
          status = HttpStatusCode.InternalServerError,
          message = ErrorResponse(
            businessCode = "INTERNAL_ERROR",
            message = "Failed to fetch last event id"
          ),
        )
      }
    )
  }
}
