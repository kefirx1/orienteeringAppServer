package pl.dev.bkwiatkowski.controller.maps.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.controller.maps.dto.request.AddMapRequestDto
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.domain.model.MapWaypoint
import pl.dev.bkwiatkowski.domain.usecase.AddMapUC

class AddMapHandler(
  private val addMapUC: AddMapUC,
) {
  suspend fun handle(call: ApplicationCall) {
    val request = either {
      call.receiveNullable<AddMapRequestDto>()
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

    val waypoints = request.waypoints.map { waypointDto ->
      MapWaypoint(
        label = waypointDto.label,
        coordinateX = waypointDto.coordinateX,
        coordinateY = waypointDto.coordinateY,
      )
    }

    addMapUC(
      params = AddMapUC.Params(
        name = request.name,
        description = request.description,
        imageData = request.imageData,
        canPlayManually = request.canPlayManually,
        waypoints = waypoints,
      )
    ).fold(
      onRight = { mapId ->
        call.respond(
          status = HttpStatusCode.Created,
          message = mapOf("id" to mapId),
        )
      },
      onLeft = {
        call.respond(
          status = HttpStatusCode.InternalServerError,
          message = ErrorResponse(
            businessCode = "MAP_CREATION_FAILED",
            message = "Failed to create map"
          )
        )
      }
    )
  }
}
