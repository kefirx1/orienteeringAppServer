package pl.dev.bkwiatkowski.controller.events.dto.response

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class SessionWaypointDetailWebDto(
  val waypointId: Int,
  @Contextual
  val visitedAt: LocalDateTime,
  val label: String?,
  val imagePath: String?,
)
