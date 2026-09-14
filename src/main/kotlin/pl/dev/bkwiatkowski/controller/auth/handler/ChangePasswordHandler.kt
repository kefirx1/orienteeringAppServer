package pl.dev.bkwiatkowski.controller.auth.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.controller.auth.dto.request.ChangePasswordRequestDto
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.core.security.token.USER_ID_CLAIM
import pl.dev.bkwiatkowski.domain.model.SaltedHash
import pl.dev.bkwiatkowski.domain.usecase.ChangePasswordUC
import pl.dev.bkwiatkowski.domain.usecase.GetAdminPanelUserByIdUC
import pl.dev.bkwiatkowski.domain.usecase.VerifyAdminPanelUserAuthenticationUC

class ChangePasswordHandler(
  private val changePasswordUC: ChangePasswordUC,
  private val getAdminPanelUserByIdUC: GetAdminPanelUserByIdUC,
  private val verifyAdminPanelUserAuthenticationUC: VerifyAdminPanelUserAuthenticationUC,
) {
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

    val request = try {
      call.receiveNullable<ChangePasswordRequestDto>()
    } catch (e: Exception) {
      call.respond(
        status = HttpStatusCode.BadRequest,
        message = ErrorResponse(
          businessCode = "INVALID_REQUEST",
          message = "Invalid or missing request body"
        )
      )
      return
    }

    if (request == null || request.oldPassword.isBlank() || request.newPassword.isBlank()) {
      call.respond(
        status = HttpStatusCode.BadRequest,
        message = ErrorResponse(
          businessCode = "INVALID_PASSWORD",
          message = "Old password and new password cannot be empty"
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
          businessCode = "USER_NOT_FOUND",
          message = "User not found"
        )
      )
      return
    }

    verifyAdminPanelUserAuthenticationUC(
      params = VerifyAdminPanelUserAuthenticationUC.Params(
        username = user.username,
        password = request.oldPassword,
        saltedHash = SaltedHash(
          hash = user.password,
          salt = user.salt,
        ),
      ),
    ).onRight { verifyResult ->
      if (verifyResult == VerifyAdminPanelUserAuthenticationUC.Result.InvalidCredentials) {
        call.respond(
          status = HttpStatusCode.Forbidden,
          message = ErrorResponse(
            businessCode = "INVALID_OLD_PASSWORD",
            message = "Old password is incorrect"
          )
        )
        return
      }
    }.getRightOrElse {
      call.respond(
        status = HttpStatusCode.InternalServerError,
        message = ErrorResponse(
          businessCode = "AUTHENTICATION_ERROR",
          message = "Failed to verify authentication"
        )
      )
      return
    }

    changePasswordUC(
      params = ChangePasswordUC.Params(
        userId = userId,
        newPassword = request.newPassword
      )
    ).getRightOrElse {
      call.respond(
        status = HttpStatusCode.InternalServerError,
        message = ErrorResponse(
          businessCode = "PASSWORD_CHANGE_ERROR",
          message = "Failed to change password"
        )
      )
      return
    }

    call.respond(HttpStatusCode.OK)
  }
}
