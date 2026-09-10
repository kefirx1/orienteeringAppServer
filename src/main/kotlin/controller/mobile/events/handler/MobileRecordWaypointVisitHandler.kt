package pl.dev.bkwiatkowski.controller.mobile.events.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.controller.mobile.events.dto.request.WebsocketWaypointVisitDto
import pl.dev.bkwiatkowski.controller.mobile.events.dto.response.SessionWaypointDetailDto
import pl.dev.bkwiatkowski.controller.mobile.events.dto.response.WebsocketWaypointVisitResponseDto
import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Log
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.core.security.token.USER_ID_CLAIM
import pl.dev.bkwiatkowski.domain.usecase.RecordWaypointVisitUC
import java.time.LocalDateTime

class MobileRecordWaypointVisitHandler(
  private val recordWaypointVisitUC: RecordWaypointVisitUC,
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
          businessCode = "MISSING_SESSION_UUID",
          message = "Missing sessionUuid parameter"
        )
      )
      return
    }

    val dto = either {
      call.receive<WebsocketWaypointVisitDto>()
    }.getRightOrNull() ?: run {
      call.respond(
        status = HttpStatusCode.BadRequest,
        message = ErrorResponse(
          businessCode = "INVALID_BODY",
          message = "Failed to parse request body as WebsocketWaypointVisitDto",
        )
      )
      return
    }

    either {
      val visitedAt = dto.visitedAt ?: LocalDateTime.now()

      val imagePath = dto.imagePath.takeIf { it.isNotBlank() }
      if ((imagePath != null) && !imagePath.startsWith(prefix = sessionUuid)) {
        call.respond(
          status = HttpStatusCode.BadRequest,
          message = ErrorResponse(
            businessCode = "INVALID_IMAGE_PATH",
            message = "Provided image path does not belong to this session",
          )
        )
        return
      }

      val savedDetail = recordWaypointVisitUC(
        params = RecordWaypointVisitUC.Params(
          sessionUuid = sessionUuid,
          userId = userId,
          waypointId = dto.waypointId,
          visitedAt = visitedAt,
          imagePath = imagePath!!,
        )
      ).getRight()

      val lastDetailDto = SessionWaypointDetailDto(
        waypointId = savedDetail.waypointId,
        visitedAt = savedDetail.visitedAt,
      )

      val response = WebsocketWaypointVisitResponseDto(
        lastVisitedWaypoint = lastDetailDto,
      )

      call.respond(
        status = HttpStatusCode.OK,
        message = response,
      )
    }.onLeft { error ->
      Log.error(
        message = "Failed to record waypoint visit",
        throwable = (error as? DomainError.Custom)?.e,
      )

      call.respond(
        status = HttpStatusCode.InternalServerError,
        message = ErrorResponse(
          businessCode = "RECORD_WAYPOINT_FAILED",
          message = "Failed to record waypoint visit",
        )
      )
    }
  }
}
