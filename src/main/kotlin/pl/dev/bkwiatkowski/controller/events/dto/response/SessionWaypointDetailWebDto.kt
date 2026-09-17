package pl.dev.bkwiatkowski.controller.events.dto.response

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import pl.dev.bkwiatkowski.controller.mobile.events.dto.AccuracyDto
import java.time.LocalDateTime

@Serializable
data class SessionWaypointDetailWebDto(
  val waypointId: Int,
  @Contextual
  val visitedAt: LocalDateTime,
  val accuracy: AccuracyDto,
  val label: String?,
  val imagePath: String?,
)
