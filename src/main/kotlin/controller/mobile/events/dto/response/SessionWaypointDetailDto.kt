package pl.dev.bkwiatkowski.controller.mobile.events.dto.response

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class SessionWaypointDetailDto(
  val id: Int,
  val waypointId: Int,
  @Contextual
  val visitedAt: LocalDateTime,
)
