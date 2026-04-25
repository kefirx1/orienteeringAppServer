package pl.dev.bkwiatkowski.controller.maps.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.controller.maps.dto.response.MapListResponseDto
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.domain.usecase.GetAllMapsUC

class MapListHandler(
  private val getAllMapsUC: GetAllMapsUC,
) {
  suspend fun handle(call: ApplicationCall) {
    getAllMapsUC(params = GetAllMapsUC.Params).fold(
      onRight = { maps ->
        val response = maps.map { map ->
          MapListResponseDto(
            id = map.id,
            name = map.name,
            description = map.description,
            imageUri = map.imageUri,
          )
        }
        call.respond(
          status = HttpStatusCode.OK,
          message = response,
        )
      },
      onLeft = {
        call.respond(
          status = HttpStatusCode.InternalServerError,
          message = ErrorResponse(
            businessCode = "INTERNAL_ERROR",
            message = "Failed to fetch maps"
          ),
        )
      }
    )
  }
}
