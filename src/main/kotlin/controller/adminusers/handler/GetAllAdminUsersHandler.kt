package pl.dev.bkwiatkowski.controller.adminusers.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.controller.adminusers.dto.response.toResponseDto
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.core.security.token.USER_ID_CLAIM
import pl.dev.bkwiatkowski.core.security.token.USER_ROLE_CLAIM
import pl.dev.bkwiatkowski.domain.model.AdminPanelUser
import pl.dev.bkwiatkowski.domain.usecase.GetAllAdminPanelUsersUC

class GetAllAdminUsersHandler(
  private val getAllAdminPanelUsersUC: GetAllAdminPanelUsersUC,
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

    getAllAdminPanelUsersUC(params = GetAllAdminPanelUsersUC.Params)
      .fold(
        onRight = { users ->
          call.respond(
            status = HttpStatusCode.OK,
            message = users.map { it.toResponseDto() },
          )
        },
        onLeft = {
          call.respond(
            status = HttpStatusCode.InternalServerError,
            message = ErrorResponse(
              businessCode = "FETCH_FAILED",
              message = "Failed to fetch users"
            )
          )
        }
      )
  }
}
