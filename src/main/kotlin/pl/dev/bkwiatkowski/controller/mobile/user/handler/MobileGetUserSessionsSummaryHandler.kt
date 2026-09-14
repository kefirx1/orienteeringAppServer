package pl.dev.bkwiatkowski.controller.mobile.user.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.controller.mobile.user.dto.response.UserSessionsSummaryResponseDto
import pl.dev.bkwiatkowski.controller.mobile.user.dto.response.UserSessionSummaryDto
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.core.security.token.USER_ID_CLAIM
import pl.dev.bkwiatkowski.domain.usecase.GetUserSessionsSummaryUC

class MobileGetUserSessionsSummaryHandler(
  private val getUserSessionsSummaryUC: GetUserSessionsSummaryUC,
) {
  suspend fun handle(call: ApplicationCall) {
    val principal = call.principal<JWTPrincipal>()
    val jwtUserId = principal?.payload?.getClaim(USER_ID_CLAIM)?.asString()?.toIntOrNull()

    if (jwtUserId == null) {
      call.respond(
        status = HttpStatusCode.Unauthorized,
        message = ErrorResponse(
          businessCode = "UNAUTHORIZED",
          message = "User is not authenticated",
        )
      )
      return
    }

    val userIdParam = call.parameters["userId"]
    val requestedUserId = userIdParam?.toIntOrNull()

    if (requestedUserId == null) {
      call.respond(
        status = HttpStatusCode.BadRequest,
        message = ErrorResponse(
          businessCode = "INVALID_REQUEST",
          message = "Missing or invalid userId path parameter",
        )
      )
      return
    }

    if (requestedUserId != jwtUserId) {
      call.respond(
        status = HttpStatusCode.Forbidden,
        message = ErrorResponse(
          businessCode = "INVALID_USER",
          message = "User id in path does not match authenticated user",
        )
      )
      return
    }

    getUserSessionsSummaryUC(
      params = GetUserSessionsSummaryUC.Params(userId = requestedUserId)
    ).fold(
      onRight = { sessions ->
        val dto = sessions.map { s ->
          UserSessionSummaryDto(
            sessionUuid = s.sessionUuid,
            startedAt = s.startedAt,
            finishedAt = s.finishedAt,
            visitedWaypointsCount = s.visitedWaypointsCount,
            mapName = s.mapName,
            eventName = s.eventName,
          )
        }

        call.respond(
          status = HttpStatusCode.OK,
          message = UserSessionsSummaryResponseDto(sessions = dto),
        )
      },
      onLeft = {
        call.respond(
          status = HttpStatusCode.InternalServerError,
          message = ErrorResponse(
            businessCode = "FETCH_USER_SESSIONS_FAILED",
            message = "Failed to fetch user sessions",
          )
        )
      }
    )
  }
}
