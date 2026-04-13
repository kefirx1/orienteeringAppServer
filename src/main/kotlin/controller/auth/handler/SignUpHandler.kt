package pl.dev.bkwiatkowski.controller.auth.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.controller.auth.dto.request.SignUpRequestDto
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.core.validation.ValidationState
import pl.dev.bkwiatkowski.domain.usecase.AddNewAdminPanelUserUC
import pl.dev.bkwiatkowski.domain.usecase.ValidateAdminPanelUserRequestUC

class SignUpHandler(
  private val addNewAdminPanelUserUC: AddNewAdminPanelUserUC,
  private val validateAdminPanelUserRequestUC: ValidateAdminPanelUserRequestUC,
) {
  suspend fun handle(call: ApplicationCall) {
    val request = runCatching { call.receiveNullable<SignUpRequestDto>() }.getOrNull() ?: run {
      call.respond(
        status = HttpStatusCode.BadRequest,
        message = ErrorResponse(
          businessCode = "INVALID_REQUEST",
          message = "Invalid or missing request body"
        )
      )
      return
    }

    val validationResult = validateAdminPanelUserRequestUC(
      params = ValidateAdminPanelUserRequestUC.Params(
        username = request.username,
        email = request.email,
        password = request.password,
      )
    ).getRightOrElse {
      call.respond(
        status = HttpStatusCode.InternalServerError,
        message = ErrorResponse(
          businessCode = "INTERNAL_ERROR",
          message = "An unexpected error occurred during validation"
        )
      )
      return
    }

    if (validationResult is ValidationState.Invalid) {
      call.respond(
        status = HttpStatusCode.Conflict,
        message = ErrorResponse(
          businessCode = "VALIDATION_FAILED",
          message = validationResult.message
        )
      )
      return
    }

    addNewAdminPanelUserUC(
      params = AddNewAdminPanelUserUC.Params(
        username = request.username,
        email = request.email,
        password = request.password,
      ),
    ).fold(
      onRight = {
        call.respond(HttpStatusCode.OK)
      },
      onLeft = {
        call.respond(
          status = HttpStatusCode.Conflict,
          message = ErrorResponse(
            businessCode = "USER_CREATION_FAILED",
            message = "Failed to create user"
          )
        )
      }
    )
  }
}
