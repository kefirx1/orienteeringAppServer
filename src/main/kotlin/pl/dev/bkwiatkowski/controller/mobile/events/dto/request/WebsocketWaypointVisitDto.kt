package pl.dev.bkwiatkowski.controller.mobile.events.dto.request

import kotlinx.serialization.Serializable
import kotlinx.serialization.Contextual
import java.time.LocalDateTime

@Serializable
data class WebsocketWaypointVisitDto(
  val waypointId: Int,
  @Contextual
  val visitedAt: LocalDateTime? = null,
  val imagePath: String,
)
