package pl.dev.bkwiatkowski.controller.mobile.events.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.controller.mobile.events.dto.response.MobileEventListResponseDto
import pl.dev.bkwiatkowski.controller.mobile.events.dto.response.MobileMapDto
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.core.security.token.USER_ID_CLAIM
import pl.dev.bkwiatkowski.domain.usecase.GetAdminPanelUserByIdUC
import pl.dev.bkwiatkowski.domain.usecase.GetAllEventsUC

class MobileEventListHandler(
  private val getAllEventsUC: GetAllEventsUC,
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

    getAllEventsUC(
      params = GetAllEventsUC.Params(
        userId = userId,
        isAdmin = true,
      )
    ).fold(
      onRight = { events ->
        val response = events.map { event ->
          val creatorUsername = getAdminPanelUserByIdUC(
            params = GetAdminPanelUserByIdUC.Params(id = event.userId)
          ).getRightOrNull()?.username ?: "unknown"

          MobileEventListResponseDto(
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
            createdByUsername = creatorUsername,
            eventType = event.eventType,
          )
        }
        println(response)
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
            message = "Failed to fetch events"
          ),
        )
      }
    )
  }
}
