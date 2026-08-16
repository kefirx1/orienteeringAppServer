package pl.dev.bkwiatkowski.controller.mobile.events.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.controller.mobile.events.dto.response.FinishSessionResponseDto
import pl.dev.bkwiatkowski.controller.mobile.events.dto.response.SessionParticipantDto
import pl.dev.bkwiatkowski.controller.mobile.events.dto.response.SessionWaypointDetailDto
import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Log
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.core.security.token.USER_ID_CLAIM
import pl.dev.bkwiatkowski.domain.usecase.FinishSessionUC
import pl.dev.bkwiatkowski.domain.usecase.GetUserSessionWaypointDetailsUC
import java.time.LocalDateTime

class MobileFinishSessionHandler(
  private val finishSessionUC: FinishSessionUC,
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
          message = "User is not authenticated"
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
          message = "Missing or invalid sessionUuid path parameter"
        )
      )
      return
    }

    finishSessionUC(
      params = FinishSessionUC.Params(
        sessionUuid = sessionUuid,
        userId = userId,
        finishedAt = LocalDateTime.now(),
      )
    ).fold(
      onRight = { participant ->
        getUserSessionWaypointDetailsUC(
          params = GetUserSessionWaypointDetailsUC.Params(
            sessionUuid = sessionUuid,
            userId = userId,
          )
        ).fold(
          onRight = { waypointDetails ->
            val participantDto = SessionParticipantDto(
              sessionUuid = participant.sessionUuid,
              joinedAt = participant.joinedAt,
              finishedAt = participant.finishedAt!!,
            )

            val waypointDto = waypointDetails.map { d ->
              SessionWaypointDetailDto(
                waypointId = d.waypointId,
                visitedAt = d.visitedAt,
              )
            }

            call.respond(message = FinishSessionResponseDto(participant = participantDto, sessionWaypointDetails = waypointDto))
          },
          onLeft = { error ->
            call.respond(
              status = HttpStatusCode.BadRequest,
              message = ErrorResponse(
                businessCode = "FINISH_SESSION_FAILED",
                message = "Failed to fetch waypoint details",
              )
            )
          }
        )
      },
      onLeft = { error ->
        Log.error(
          message = "${(error as? DomainError.Custom)?.e}",
        )
        call.respond(
          status = HttpStatusCode.BadRequest,
          message = ErrorResponse(
            businessCode = "FINISH_SESSION_FAILED",
            message = "Failed to finish session",
          ),
        )
      }
    )
  }
}