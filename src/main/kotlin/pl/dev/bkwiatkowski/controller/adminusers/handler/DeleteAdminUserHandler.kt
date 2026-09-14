package pl.dev.bkwiatkowski.controller.adminusers.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.core.security.token.USER_ID_CLAIM
import pl.dev.bkwiatkowski.core.security.token.USER_ROLE_CLAIM
import pl.dev.bkwiatkowski.domain.model.AdminPanelUser
import pl.dev.bkwiatkowski.domain.usecase.DeleteAdminPanelUserUC

class DeleteAdminUserHandler(
  private val deleteAdminPanelUserUC: DeleteAdminPanelUserUC,
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

    val targetUserId = call.parameters["id"]?.toIntOrNull()
    if (targetUserId == null) {
      call.respond(
        status = HttpStatusCode.BadRequest,
        message = ErrorResponse(
          businessCode = "INVALID_ID",
          message = "Invalid user ID"
        )
      )
      return
    }

    if (targetUserId == userId) {
      call.respond(
        status = HttpStatusCode.BadRequest,
        message = ErrorResponse(
          businessCode = "CANNOT_DELETE_SELF",
          message = "You cannot delete your own account"
        )
      )
      return
    }

    deleteAdminPanelUserUC(params = DeleteAdminPanelUserUC.Params(userId = targetUserId))
      .fold(
        onRight = {
          call.respond(HttpStatusCode.OK)
        },
        onLeft = {
          call.respond(
            status = HttpStatusCode.NotFound,
            message = ErrorResponse(
              businessCode = "USER_NOT_FOUND",
              message = "User with given ID does not exist"
            )
          )
        }
      )
  }
}
