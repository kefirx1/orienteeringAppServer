package pl.dev.bkwiatkowski.controller.maps.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.domain.usecase.DeleteMapUC

class DeleteMapHandler(
  private val deleteMapUC: DeleteMapUC,
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

    deleteMapUC(params = DeleteMapUC.Params(mapId = mapId)).fold(
      onRight = {
        call.respond(HttpStatusCode.OK)
      },
      onLeft = {
        call.respond(
          status = HttpStatusCode.NotFound,
          message = ErrorResponse(
            businessCode = "MAP_NOT_FOUND",
            message = "Map not found"
          )
        )
      }
    )
  }
}
