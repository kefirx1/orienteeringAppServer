package pl.dev.bkwiatkowski.controller.settings.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.controller.settings.dto.response.SettingsResponseDto
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.core.security.token.USER_ID_CLAIM
import pl.dev.bkwiatkowski.domain.usecase.GetAdminPanelUserByIdUC

class SettingsHandler(
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
        status = HttpStatusCode.NotFound,
        message = ErrorResponse(
          businessCode = "USER_NOT_FOUND",
          message = "User does not exist"
        )
      )
      return
    }

    call.respond(
      status = HttpStatusCode.OK,
      message = SettingsResponseDto(
        username = user.username,
      )
    )
  }
}

