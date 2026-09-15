package pl.dev.bkwiatkowski.controller.maps.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.core.security.token.USER_ID_CLAIM
import pl.dev.bkwiatkowski.core.security.token.USER_ROLE_CLAIM
import pl.dev.bkwiatkowski.domain.model.AdminPanelUser
import pl.dev.bkwiatkowski.domain.usecase.DeleteMapUC

class DeleteMapHandler(
  private val deleteMapUC: DeleteMapUC,
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
          message = "Only admins can delete maps"
        )
      )
      return
    }

    val mapId = call.parameters["id"]?.toIntOrNull()
    if (mapId == null) {
      call.respond(
        status = HttpStatusCode.BadRequest,
        message = ErrorResponse(
          businessCode = "INVALID_ID",
          message = "Invalid map ID"
        )
      )
      return
    }

    deleteMapUC(params = DeleteMapUC.Params(mapId = mapId)).fold(
      onRight = {
        call.respond(HttpStatusCode.OK)
      },
      onLeft = { domainError ->
        when (domainError) {
          is DomainError.Custom -> {
            when (domainError.e) {
              is NullPointerException -> call.respond(
                status = HttpStatusCode.NotFound,
                message = ErrorResponse(
                  businessCode = "MAP_NOT_FOUND",
                  message = "Map not found"
                )
              )
              is IllegalStateException -> call.respond(
                status = HttpStatusCode.Conflict,
                message = ErrorResponse(
                  businessCode = "MAP_HAS_EVENTS",
                  message = "Map cannot be deleted because there are events attached to it"
                )
              )
              else -> call.respond(
                status = HttpStatusCode.InternalServerError,
                message = ErrorResponse(
                  businessCode = "UNKNOWN_ERROR",
                  message = "An unexpected error occurred"
                )
              )
            }
          }
        }
      }
    )
  }
}
