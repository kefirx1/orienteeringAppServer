package pl.dev.bkwiatkowski.controller.mobile.auth.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.controller.mobile.auth.dto.request.MobileSignUpRequestDto
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.core.validation.ValidationState
import pl.dev.bkwiatkowski.domain.usecase.AddNewMobileUserUC
import pl.dev.bkwiatkowski.domain.usecase.ValidateMobileUserRegistrationUC

class MobileSignUpHandler(
  private val addNewMobileUserUC: AddNewMobileUserUC,
  private val validateMobileUserRegistrationUC: ValidateMobileUserRegistrationUC,
) {
  suspend fun handle(call: ApplicationCall) {
    val request = either {
      call.receiveNullable<MobileSignUpRequestDto>()
    }.getRightOrNull() ?: run {
      call.respond(
        status = HttpStatusCode.BadRequest,
        message = ErrorResponse(
          businessCode = "INVALID_REQUEST",
          message = "Invalid or missing request body",
        )
      )
      return
    }

    val validationResult = validateMobileUserRegistrationUC(
      params = ValidateMobileUserRegistrationUC.Params(
        username = request.username,
        email = request.email,
        password = request.password,
      )
    ).getRightOrElse {
      call.respond(
        status = HttpStatusCode.InternalServerError,
        message = ErrorResponse(
          businessCode = "INTERNAL_ERROR",
          message = "An unexpected error occurred during validation",
        )
      )
      return
    }

    if (validationResult is ValidationState.Invalid) {
      call.respond(
        status = HttpStatusCode.Conflict,
        message = ErrorResponse(
          businessCode = "VALIDATION_FAILED",
          message = validationResult.message,
        )
      )
      return
    }

    addNewMobileUserUC(
      params = AddNewMobileUserUC.Params(
        username = request.username,
        email = request.email,
        password = request.password,
        phoneNumber = request.phoneNumber,
        dateOfBirth = request.dateOfBirth,
      )
    ).fold(
      onRight = { call.respond(HttpStatusCode.Created) },
      onLeft = {
        call.respond(
          status = HttpStatusCode.Conflict,
          message = ErrorResponse(
            businessCode = "USER_CREATION_FAILED",
            message = "Failed to create user",
          )
        )
      }
    )
  }
}
