package pl.dev.bkwiatkowski.controller.maps.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.controller.maps.dto.response.MapDetailResponseDto
import pl.dev.bkwiatkowski.controller.maps.dto.response.WaypointResponseDto
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.domain.usecase.GetMapByIdUC

class MapDetailHandler(
  private val getMapByIdUC: GetMapByIdUC,
) {
  suspend fun handle(call: ApplicationCall) {
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

    getMapByIdUC(params = GetMapByIdUC.Params(mapId = mapId)).fold(
      onRight = { map ->
         val response = MapDetailResponseDto(
           id = map.id,
           name = map.name,
           description = map.description,
           imageData = map.imageData,
           waypoints = map.mapWaypoints.map { waypoint ->
            WaypointResponseDto(
              id = waypoint.id,
              label = waypoint.label,
              coordinateX = waypoint.coordinateX,
              coordinateY = waypoint.coordinateY,
            )
          }
        )
        call.respond(
          status = HttpStatusCode.OK,
          message = response,
        )
      },
      onLeft = {
        call.respond(
          status = HttpStatusCode.NotFound,
          message = ErrorResponse(
            businessCode = "MAP_NOT_FOUND",
            message = "Map not found"
          ),
        )
      }
    )
  }
}
