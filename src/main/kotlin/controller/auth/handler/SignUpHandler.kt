package pl.dev.bkwiatkowski.controller.auth.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.controller.auth.dto.request.SignUpRequestDto
import pl.dev.bkwiatkowski.core.validation.ValidationState
import pl.dev.bkwiatkowski.domain.usecase.AddNewAdminPanelUserUC
import pl.dev.bkwiatkowski.domain.usecase.ValidateAdminPanelUserRequestUC

class SignUpHandler(
  private val addNewAdminPanelUserUC: AddNewAdminPanelUserUC,
  private val validateAdminPanelUserRequestUC: ValidateAdminPanelUserRequestUC,
) {
  suspend fun handle(call: ApplicationCall) {
    val request = runCatching { call.receiveNullable<SignUpRequestDto>() }.getOrNull() ?: run {
      call.respond(HttpStatusCode.BadRequest)
      return
    }

    val validationResult = validateAdminPanelUserRequestUC(
      params = ValidateAdminPanelUserRequestUC.Params(
        username = request.username,
        email = request.email,
        password = request.password,
      )
    ).getRightOrElse {
      call.respond(HttpStatusCode.InternalServerError)
      return
    }

    if (validationResult is ValidationState.Invalid) {
      call.respond(
        status = HttpStatusCode.Conflict,
        message = validationResult.errorMessage
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
        call.respond(HttpStatusCode.Conflict)
      }
    )
  }
}