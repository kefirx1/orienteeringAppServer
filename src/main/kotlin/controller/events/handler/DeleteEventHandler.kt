package pl.dev.bkwiatkowski.controller.events.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.core.security.token.USER_ID_CLAIM
import pl.dev.bkwiatkowski.domain.model.AdminPanelUser
import pl.dev.bkwiatkowski.domain.usecase.DeleteEventUC
import pl.dev.bkwiatkowski.domain.usecase.GetAdminPanelUserByIdUC

class DeleteEventHandler(
  private val deleteEventUC: DeleteEventUC,
  private val getAdminPanelUserByIdUC: GetAdminPanelUserByIdUC,
) {
  suspend fun handle(call: ApplicationCall) {
    val principal = call.principal<JWTPrincipal>()
    val userId = principal?.payload?.getClaim(USER_ID_CLAIM)?.asString()?.toIntOrNull()

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

    val user = getAdminPanelUserByIdUC(
      params = GetAdminPanelUserByIdUC.Params(id = userId)
    ).getRightOrElse {
      call.respond(
        status = HttpStatusCode.NotFound,
        message = ErrorResponse(
          businessCode = "USER_NOT_FOUND",
          message = "User does not exist"
        )
      )
      return
    }

    val eventId = call.parameters["id"]?.toIntOrNull()
    if (eventId == null) {
      call.respond(
        status = HttpStatusCode.BadRequest,
        message = ErrorResponse(
          businessCode = "INVALID_ID",
          message = "Invalid event ID"
        )
      )
      return
    }

    deleteEventUC(
      params = DeleteEventUC.Params(
        eventId = eventId,
        userId = userId,
        isAdmin = user.role == AdminPanelUser.Role.ADMIN
      )
    ).fold(
      onRight = {
        call.respond(HttpStatusCode.OK)
      },
      onLeft = {
        call.respond(
          status = HttpStatusCode.Forbidden,
          message = ErrorResponse(
            businessCode = "DELETION_FORBIDDEN",
            message = "You do not have permission to delete this event"
          )
        )
      }
    )
  }
}
