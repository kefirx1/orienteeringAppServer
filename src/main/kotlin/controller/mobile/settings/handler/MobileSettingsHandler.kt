package pl.dev.bkwiatkowski.controller.mobile.settings.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.controller.mobile.settings.dto.response.MobileSettingsResponseDto
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.core.security.token.USER_ID_CLAIM
import java.time.LocalDateTime

class MobileSettingsHandler {
  suspend fun handle(call: ApplicationCall) {
    val principal = call.principal<JWTPrincipal>()
    val userId = principal?.payload?.getClaim(USER_ID_CLAIM)?.asString()?.toIntOrNull()

    if (userId == null) {
      call.respond(
        status = HttpStatusCode.Forbidden,
        message = ErrorResponse(
          businessCode = "FORBIDDEN",
          message = "User is not authenticated"
        )
      )
      return
    }

    val localDateTime = LocalDateTime.now()

    call.respond(
      status = HttpStatusCode.OK,
      message = MobileSettingsResponseDto(
        userId = userId,
        serverLocalDateTime = localDateTime,
      )
    )
  }
}
