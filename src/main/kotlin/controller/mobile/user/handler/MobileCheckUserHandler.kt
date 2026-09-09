package pl.dev.bkwiatkowski.controller.mobile.user.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.controller.mobile.user.dto.response.CheckUserResponseDto
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.core.security.token.USER_ID_CLAIM
import pl.dev.bkwiatkowski.domain.usecase.GetMobileUserUC
import pl.dev.bkwiatkowski.domain.usecase.GetMobileUserByIdUC

class MobileCheckUserHandler(
  private val getMobileUserUC: GetMobileUserUC,
  private val getMobileUserByIdUC: GetMobileUserByIdUC,
) {
  suspend fun handle(call: ApplicationCall) {
    val principal = call.principal<JWTPrincipal>()
    val jwtUserId = principal?.payload?.getClaim(USER_ID_CLAIM)?.asString()?.toIntOrNull()

    if (jwtUserId == null) {
      call.respond(
        status = HttpStatusCode.Unauthorized,
        message = ErrorResponse(
          businessCode = "UNAUTHORIZED",
          message = "User is not authenticated",
        )
      )
      return
    }

    val usernameParam = call.parameters["username"]

    if (usernameParam.isNullOrBlank()) {
      call.respond(
        status = HttpStatusCode.BadRequest,
        message = ErrorResponse(
          businessCode = "INVALID_USERNAME",
          message = "Username path parameter cannot be empty",
        )
      )
      return
    }

    val requester = getMobileUserByIdUC(
      params = GetMobileUserByIdUC.Params(id = jwtUserId)
    ).getRightOrElse {
      call.respond(
        status = HttpStatusCode.Forbidden,
        message = ErrorResponse(
          businessCode = "USER_NOT_FOUND",
          message = "Authenticated user not found",
        )
      )
      return
    }

    if (usernameParam == requester.username) {
      call.respond(
        status = HttpStatusCode.Forbidden,
        message = ErrorResponse(
          businessCode = "INVALID_REQUEST",
          message = "Cannot check requesting user's own username",
        )
      )
      return
    }

    getMobileUserUC(
      params = GetMobileUserUC.Params(username = usernameParam),
    ).fold(
      onRight = { user ->
        call.respond(
          status = HttpStatusCode.OK,
          message = CheckUserResponseDto(username = user.username),
        )
      },
      onLeft = {
        call.respond(
          status = HttpStatusCode.NotFound,
          message = ErrorResponse(
            businessCode = "USER_NOT_FOUND",
            message = "User does not exist",
          )
        )
      }
    )
  }
}
