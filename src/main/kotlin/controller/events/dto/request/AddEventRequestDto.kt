package pl.dev.bkwiatkowski.controller.events.dto.request

import kotlinx.serialization.Serializable
import kotlinx.serialization.Contextual
import java.time.LocalDateTime

@Serializable
data class AddEventRequestDto(
  val mapId: Int,
  val name: String,
  val description: String,
  @Contextual
  val startDateTime: LocalDateTime,
  val startLocationX: Float,
  val startLocationY: Float,
  val waypointIds: List<Int>,
)
