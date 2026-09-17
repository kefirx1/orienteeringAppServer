package pl.dev.bkwiatkowski.controller.mobile.events.dto.response

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import pl.dev.bkwiatkowski.controller.mobile.events.dto.AccuracyDto
import java.time.LocalDateTime

@Serializable
data class SessionWaypointDetailDto(
  val waypointId: Int,
  @Contextual
  val visitedAt: LocalDateTime,
  val accuracy: AccuracyDto,
)