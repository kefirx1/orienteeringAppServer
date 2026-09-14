package pl.dev.bkwiatkowski.controller.admin.events.dto.response

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class EventParticipantProgressionDto(
  val participantId: Int,
  val userId: Int,
  val userName: String,
  @Contextual val startedAt: LocalDateTime,
  @Contextual val finishedAt: LocalDateTime?,
  val visitedWaypointsCount: Int,
)
