package pl.dev.bkwiatkowski.controller.events.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.controller.events.dto.request.AddEventRequestDto
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.core.security.token.USER_ID_CLAIM
import pl.dev.bkwiatkowski.domain.usecase.AddEventUC
import pl.dev.bkwiatkowski.domain.model.EventType
import pl.dev.bkwiatkowski.core.either

class AddEventHandler(
  private val addEventUC: AddEventUC,
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

    val request = either {
      call.receiveNullable<AddEventRequestDto>()
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

    if (request.name.isBlank() || request.description.isBlank()) {
      call.respond(
        status = HttpStatusCode.BadRequest,
        message = ErrorResponse(
          businessCode = "INVALID_REQUEST",
          message = "Name and description cannot be empty"
        )
      )
      return
    }

    val allowOfflineTracking = if (request.eventType == EventType.OFFLINE) true else request.allowOfflineTracking

    addEventUC(
      params = AddEventUC.Params(
        mapId = request.mapId,
        userId = userId,
        name = request.name,
        description = request.description,
        startDate = request.startDateTime,
        startLocationX = request.startLocationX,
        startLocationY = request.startLocationY,
        waypointIds = request.waypointIds,
        allowOfflineTracking = allowOfflineTracking,
        eventType = request.eventType,
      )
    ).fold(
      onRight = { eventId ->
        call.respond(
          status = HttpStatusCode.Created,
          message = mapOf("id" to eventId),
        )
      },
      onLeft = {
        call.respond(
          status = HttpStatusCode.InternalServerError,
          message = ErrorResponse(
            businessCode = "EVENT_CREATION_FAILED",
            message = "Failed to create event"
          )
        )
      }
    )
  }
}
