package pl.dev.bkwiatkowski.controller.auth.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.core.security.token.USER_ID_CLAIM
import pl.dev.bkwiatkowski.domain.usecase.RevokeAllUserRefreshTokensUC

class LogoutHandler(
  private val revokeAllUserRefreshTokensUC: RevokeAllUserRefreshTokensUC
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

    revokeAllUserRefreshTokensUC(
      params = RevokeAllUserRefreshTokensUC.Params(
        userId = userId
      )
    ).getRightOrElse {
      call.respond(
        status = HttpStatusCode.InternalServerError,
        message = ErrorResponse(
          businessCode = "LOGOUT_FAILED",
          message = "Failed to revoke tokens"
        )
      )
      return
    }

    call.respond(HttpStatusCode.OK)
  }
}
