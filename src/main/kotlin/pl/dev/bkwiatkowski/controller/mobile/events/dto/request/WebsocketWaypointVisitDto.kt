package pl.dev.bkwiatkowski.controller.mobile.events.dto.request

import kotlinx.serialization.Serializable
import kotlinx.serialization.Contextual
import java.time.LocalDateTime
import pl.dev.bkwiatkowski.controller.mobile.events.dto.AccuracyDto

@Serializable
data class WebsocketWaypointVisitDto(
  val waypointId: Int,
  @Contextual
  val visitedAt: LocalDateTime? = null,
  val imagePath: String,
  val accuracy: AccuracyDto,
)
