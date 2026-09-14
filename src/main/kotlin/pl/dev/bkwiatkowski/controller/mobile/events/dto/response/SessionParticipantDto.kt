package pl.dev.bkwiatkowski.controller.mobile.events.dto.response

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class SessionParticipantDto(
  val sessionUuid: String,
  @Contextual
  val joinedAt: LocalDateTime,
  @Contextual
  val finishedAt: LocalDateTime,
)
