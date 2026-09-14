package pl.dev.bkwiatkowski.controller.events.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.controller.events.dto.response.EventListResponseDto
import pl.dev.bkwiatkowski.controller.events.dto.response.MapDto
import pl.dev.bkwiatkowski.controller.maps.dto.response.WaypointResponseDto
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.core.security.token.USER_ID_CLAIM
import pl.dev.bkwiatkowski.domain.model.AdminPanelUser
import pl.dev.bkwiatkowski.domain.usecase.GetAdminPanelUserByIdUC
import pl.dev.bkwiatkowski.domain.usecase.GetAllEventsUC

class EventListHandler(
  private val getAllEventsUC: GetAllEventsUC,
  private val getAdminPanelUserByIdUC: GetAdminPanelUserByIdUC,
) {
  suspend fun handle(call: ApplicationCall) {
    val principal = call.principal<JWTPrincipal>()
    val userId = principal?.payload?.getClaim(USER_ID_CLAIM)?.asString()?.toIntOrNull()

    if (userId == null) {
      call.respond(
        status = HttpStatusCode.OK,
        message = emptyList<EventListResponseDto>(),
      )
      return
    }

    val user = getAdminPanelUserByIdUC(
      params = GetAdminPanelUserByIdUC.Params(id = userId)
    ).getRightOrElse {
      call.respond(
        status = HttpStatusCode.OK,
        message = emptyList<EventListResponseDto>(),
      )
      return
    }

    getAllEventsUC(
      params = GetAllEventsUC.Params(
        userId = userId,
        isAdmin = user.role == AdminPanelUser.Role.ADMIN
      )
    ).fold(
      onRight = { events ->
         val response = events.map { event ->
          val creatorUsername = getAdminPanelUserByIdUC(
            params = GetAdminPanelUserByIdUC.Params(id = event.userId)
          ).getRightOrNull()?.username ?: "unknown"

            EventListResponseDto(
              id = event.id,
              map = MapDto(
                id = event.map.id,
                name = event.map.name,
                description = event.map.description,
                imageData = event.map.imageData,
                mapWaypoints = event.map.mapWaypoints.map { waypoint ->
                  WaypointResponseDto(
                    id = waypoint.id,
                    label = waypoint.label,
                    coordinateX = waypoint.coordinateX,
                    coordinateY = waypoint.coordinateY,
                  )
                }
              ),
              name = event.name,
              description = event.description,
              createdAt = event.createdAt,
              startDate = event.startDate,
              startLocationX = event.startLocationX,
              startLocationY = event.startLocationY,
              createdByUsername = creatorUsername,
              eventType = event.eventType,
              eventWaypoints = event.eventWaypoints.map { waypoint ->
                WaypointResponseDto(
                  id = waypoint.id,
                  label = waypoint.label,
                  coordinateX = waypoint.coordinateX,
                  coordinateY = waypoint.coordinateY,
                )
              }
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
            message = "Failed to fetch events"
          ),
        )
      }
    )
  }
}
