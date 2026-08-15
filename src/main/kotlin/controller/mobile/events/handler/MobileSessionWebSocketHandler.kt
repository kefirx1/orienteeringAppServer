package pl.dev.bkwiatkowski.controller.mobile.events.handler

import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import pl.dev.bkwiatkowski.controller.mobile.events.dto.request.WebsocketWaypointVisitDto
import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Log
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.core.security.token.USER_ID_CLAIM
import pl.dev.bkwiatkowski.core.serialization.JsonSerializer
import pl.dev.bkwiatkowski.domain.usecase.RecordWaypointVisitUC
import pl.dev.bkwiatkowski.controller.mobile.events.dto.response.WebsocketWaypointVisitResponseDto
import java.time.LocalDateTime

class MobileSessionWebSocketHandler(
  private val recordWaypointVisitUC: RecordWaypointVisitUC,
  private val jsonSerializer: JsonSerializer,
) {
  suspend fun handle(session: DefaultWebSocketServerSession) {
    val principal = session.call.principal<JWTPrincipal>()
    val userId = principal?.payload?.getClaim(USER_ID_CLAIM)?.asString()?.toIntOrNull()

    if (userId == null) {
      session.close(
        reason = CloseReason(
          code = CloseReason.Codes.VIOLATED_POLICY,
          message = "User is not authenticated",
        ),
      )
      return
    }

    val sessionUuid = session.call.parameters["sessionUuid"]
    if (sessionUuid.isNullOrBlank()) {
      session.close(
        reason = CloseReason(
          code = CloseReason.Codes.VIOLATED_POLICY,
          message = "Missing sessionUuid",
        ),
      )
      return
    }

    either {
      mainLoop@ for (frame in session.incoming) {
        when (frame) {
          is Frame.Text -> {
            val text = frame.readText()

            either {
              val dto = jsonSerializer.deserialize(
                serialized = text,
                serializer = WebsocketWaypointVisitDto.serializer(),
              )
              val visitedAt = dto.visitedAt ?: LocalDateTime.now()

              val imagePath = dto.imagePath.takeIf { it.isNotBlank() }
              if ((imagePath != null) && !imagePath.startsWith(prefix = sessionUuid)) {
                val payload = jsonSerializer.serialize(
                  value = ErrorResponse(
                    businessCode = "INVALID_IMAGE_PATH",
                    message = "Provided image path does not belong to this session",
                  ),
                  serializer = ErrorResponse.serializer(),
                )
                session.send(Frame.Text(payload))
                continue@mainLoop
              }

              recordWaypointVisitUC(
                params = RecordWaypointVisitUC.Params(
                  sessionUuid = sessionUuid,
                  userId = userId,
                  waypointId = dto.waypointId,
                  visitedAt = visitedAt,
                  imagePath = imagePath!!,
                )
              ).getRight()

              val payload = jsonSerializer.serialize(
                value = WebsocketWaypointVisitResponseDto(
                  waypointId = dto.waypointId,
                ),
                serializer = WebsocketWaypointVisitResponseDto.serializer(),
              )

              session.send(Frame.Text(payload))
            }.onLeft { error ->
              Log.error(
                message = "Failed to record waypoint visit",
                throwable = (error as? DomainError.Custom)?.e,
              )

              either {
                val payload = jsonSerializer.serialize(
                  value = ErrorResponse(businessCode = "RECORD_WAYPOINT_FAILED", message = "Failed to record waypoint visit"),
                  serializer = ErrorResponse.serializer(),
                )
                session.send(Frame.Text(payload))
              }
            }
          }
          is Frame.Close -> {
            return
          }
          else -> {}
        }
      }
    }
  }
}
