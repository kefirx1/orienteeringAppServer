package pl.dev.bkwiatkowski.controller.events.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.controller.events.dto.response.EventDetailResponseDto
import pl.dev.bkwiatkowski.controller.events.dto.response.EventSessionDto
import pl.dev.bkwiatkowski.controller.events.dto.response.MapDto
import pl.dev.bkwiatkowski.controller.maps.dto.response.WaypointResponseDto
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.core.security.token.USER_ID_CLAIM
import pl.dev.bkwiatkowski.domain.model.AdminPanelUser
import pl.dev.bkwiatkowski.domain.usecase.GetAdminPanelUserByIdUC
import pl.dev.bkwiatkowski.domain.usecase.GetEventByIdUC

class EventDetailHandler(
  private val getEventByIdUC: GetEventByIdUC,
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
        status = HttpStatusCode.Forbidden,
        message = ErrorResponse(
          businessCode = "FORBIDDEN",
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

    getEventByIdUC(params = GetEventByIdUC.Params(eventId = eventId)).fold(
      onRight = { event ->
        val isAdmin = user.role == AdminPanelUser.Role.ADMIN
        val isEventOwner = event.userId == userId

        if (!isAdmin && !isEventOwner) {
          call.respond(
            status = HttpStatusCode.Forbidden,
            message = ErrorResponse(
              businessCode = "ACCESS_FORBIDDEN",
              message = "You do not have permission to view this event"
            )
          )
          return@fold
        }

        call.respond(
          status = HttpStatusCode.OK,
           message = EventDetailResponseDto(
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
             status = event.status,
             finishedAt = event.finishedAt,
             allowOfflineTracking = event.allowOfflineTracking,
             eventType = event.eventType,
              eventWaypoints = event.eventWaypoints.map { waypoint ->
                WaypointResponseDto(
                  id = waypoint.id,
                  label = waypoint.label,
                  coordinateX = waypoint.coordinateX,
                  coordinateY = waypoint.coordinateY,
                )
              },
              session = event.session?.let { s ->
                EventSessionDto(id = s.id, startedAt = s.startedAt, finishedAt = s.finishedAt, userCanJoin = s.userCanJoin)
              }
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
