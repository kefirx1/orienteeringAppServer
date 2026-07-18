package pl.dev.bkwiatkowski.controller.mobile.events.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.controller.mobile.events.dto.response.MobileEventDetailResponseDto
import pl.dev.bkwiatkowski.controller.mobile.events.dto.response.MobileMapDto
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.core.security.token.USER_ID_CLAIM
import pl.dev.bkwiatkowski.domain.usecase.GetEventByIdUC

class MobileEventDetailHandler(
  private val getEventByIdUC: GetEventByIdUC,
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

    getEventByIdUC(params = GetEventByIdUC.Params(eventId = eventId)).fold(
      onRight = { event ->
        call.respond(
          status = HttpStatusCode.OK,
           message = MobileEventDetailResponseDto(
             id = event.id,
             map = MobileMapDto(
               id = event.map.id,
               name = event.map.name,
               description = event.map.description,
               imageData = event.map.imageData,
             ),
             name = event.name,
             description = event.description,
             createdAt = event.createdAt,
             startDate = event.startDate,
             startLocationX = event.startLocationX,
             startLocationY = event.startLocationY,
             status = event.status,
             finishedAt = event.finishedAt,
             allowOfflineTracking = event.allowOfflineTracking,
             eventType = event.eventType,
           ),
        )
      },
      onLeft = {
        call.respond(
          status = HttpStatusCode.NotFound,
          message = ErrorResponse(
            businessCode = "EVENT_NOT_FOUND",
            message = "Event not found"
          )
        )
      }
    )
  }
}
