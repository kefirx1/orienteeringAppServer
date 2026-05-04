package pl.dev.bkwiatkowski.controller.events.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.controller.events.dto.response.EventDetailResponseDto
import pl.dev.bkwiatkowski.controller.events.dto.response.MapDto
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.domain.usecase.GetEventByIdUC

class EventDetailHandler(
  private val getEventByIdUC: GetEventByIdUC,
) {
  suspend fun handle(call: ApplicationCall) {
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
          message = EventDetailResponseDto(
            id = event.id,
            map = MapDto(
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
