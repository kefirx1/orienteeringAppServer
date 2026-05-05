package pl.dev.bkwiatkowski.controller.adminusers.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.controller.adminusers.dto.request.AddAdminUserRequestDto
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.core.security.token.USER_ID_CLAIM
import pl.dev.bkwiatkowski.core.security.token.USER_ROLE_CLAIM
import pl.dev.bkwiatkowski.core.validation.ValidationState
import pl.dev.bkwiatkowski.domain.model.AdminPanelUser
import pl.dev.bkwiatkowski.domain.usecase.AddNewAdminPanelUserUC
import pl.dev.bkwiatkowski.domain.usecase.ValidateAdminPanelUserRequestUC

class AddAdminUserHandler(
  private val addNewAdminPanelUserUC: AddNewAdminPanelUserUC,
  private val validateAdminPanelUserRequestUC: ValidateAdminPanelUserRequestUC,
) {
  suspend fun handle(call: ApplicationCall) {
    val principal = call.principal<JWTPrincipal>()
    val userId = principal?.payload?.getClaim(USER_ID_CLAIM)?.asString()?.toIntOrNull()
    val userRole = principal?.payload?.getClaim(USER_ROLE_CLAIM)?.asString()

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

    if (userRole != AdminPanelUser.Role.ADMIN.name) {
      call.respond(
        status = HttpStatusCode.Forbidden,
        message = ErrorResponse(
          businessCode = "FORBIDDEN",
          message = "Only admins can access this resource"
        )
      )
      return
    }

    val request = either {
      call.receiveNullable<AddAdminUserRequestDto>()
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
        role = request.role,
      )
    ).fold(
      onRight = {
        call.respond(HttpStatusCode.Created)
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
