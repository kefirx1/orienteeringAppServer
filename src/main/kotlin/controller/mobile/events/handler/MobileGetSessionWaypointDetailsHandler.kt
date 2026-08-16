package pl.dev.bkwiatkowski.controller.mobile.events.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.controller.mobile.events.dto.response.SessionWaypointDetailDto
import pl.dev.bkwiatkowski.controller.mobile.events.dto.response.SessionWaypointDetailsResponseDto
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.core.security.token.USER_ID_CLAIM
import pl.dev.bkwiatkowski.domain.usecase.GetUserSessionWaypointDetailsUC

class MobileGetSessionWaypointDetailsHandler(
  private val getUserSessionWaypointDetailsUC: GetUserSessionWaypointDetailsUC,
) {
  suspend fun handle(call: ApplicationCall) {
    val principal = call.principal<JWTPrincipal>()
    val userId = principal?.payload?.getClaim(USER_ID_CLAIM)?.asString()?.toIntOrNull()

    if (userId == null) {
      call.respond(
        status = HttpStatusCode.Unauthorized,
        message = ErrorResponse(
          businessCode = "UNAUTHORIZED",
          message = "User is not authenticated",
        )
      )
      return
    }

    val sessionUuid = call.parameters["sessionUuid"]
    if (sessionUuid.isNullOrBlank()) {
      call.respond(
        status = HttpStatusCode.BadRequest,
        message = ErrorResponse(
          businessCode = "INVALID_REQUEST",
          message = "Missing or invalid sessionUuid path parameter",
        )
      )
      return
    }

    getUserSessionWaypointDetailsUC(
      params = GetUserSessionWaypointDetailsUC.Params(
        sessionUuid = sessionUuid,
        userId = userId,
      )
    ).fold(
      onRight = { details ->
        val dto = details.map { detail ->
          SessionWaypointDetailDto(
            id = detail.id,
            waypointId = detail.waypointId,
            visitedAt = detail.visitedAt,
          )
        }

        call.respond(
          status = HttpStatusCode.OK,
          message = SessionWaypointDetailsResponseDto(sessionWaypointDetails = dto),
        )
      },
      onLeft = {
        call.respond(
          status = HttpStatusCode.BadRequest,
          message = ErrorResponse(
            businessCode = "FETCH_SESSION_WAYPOINT_DETAILS_FAILED",
            message = "Failed to fetch session waypoint details",
          )
        )
      }
    )
  }
}
