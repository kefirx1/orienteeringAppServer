package pl.dev.bkwiatkowski.controller.mobile.settings.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.controller.auth.dto.request.ChangePasswordRequestDto
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.core.security.token.USER_ID_CLAIM
import pl.dev.bkwiatkowski.domain.model.SaltedHash
import pl.dev.bkwiatkowski.domain.usecase.ChangeMobileUserPasswordUC
import pl.dev.bkwiatkowski.domain.usecase.GetMobileUserByIdUC
import pl.dev.bkwiatkowski.domain.usecase.VerifyMobileUserAuthenticationUC

class MobileChangePasswordHandler(
  private val changeMobileUserPasswordUC: ChangeMobileUserPasswordUC,
  private val getMobileUserByIdUC: GetMobileUserByIdUC,
  private val verifyMobileUserAuthenticationUC: VerifyMobileUserAuthenticationUC,
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

    val request = either {
      call.receiveNullable<ChangePasswordRequestDto>()
    }.getRightOrNull() ?: run {
      call.respond(
        status = HttpStatusCode.BadRequest,
        message = ErrorResponse(
          businessCode = "INVALID_REQUEST",
          message = "Invalid or missing request body"
        )
      )
      return
    }

    if (request.oldPassword.isBlank() || request.newPassword.isBlank()) {
      call.respond(
        status = HttpStatusCode.BadRequest,
        message = ErrorResponse(
          businessCode = "INVALID_PASSWORD",
          message = "Old password and new password cannot be empty"
        )
      )
      return
    }

    val user = getMobileUserByIdUC(
      params = GetMobileUserByIdUC.Params(id = userId)
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

    verifyMobileUserAuthenticationUC(
      params = VerifyMobileUserAuthenticationUC.Params(
        saltedHash = SaltedHash(
          hash = user.password,
          salt = user.salt,
        ),
        password = request.oldPassword,
      ),
    ).onRight { verifyResult ->
      if (verifyResult == VerifyMobileUserAuthenticationUC.Result.InvalidCredentials) {
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

    changeMobileUserPasswordUC(
      params = ChangeMobileUserPasswordUC.Params(
        userId = userId,
        newPassword = request.newPassword,
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
