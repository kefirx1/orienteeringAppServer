package pl.dev.bkwiatkowski.controller.events.dto.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.Contextual
import java.time.LocalDateTime

@Serializable
data class EventDetailResponseDto(
  val id: Int,
  val map: MapDto,
  val name: String,
  val description: String,
  @Contextual
  val createdAt: LocalDateTime,
  @Contextual
  val startDate: LocalDateTime,
  val startLocationX: Float,
  val startLocationY: Float,
)
