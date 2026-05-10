package pl.dev.bkwiatkowski.controller.events.dto.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.Contextual
import pl.dev.bkwiatkowski.domain.model.EventType
import java.time.LocalDateTime

@Serializable
data class EventListResponseDto(
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
  val createdByUsername: String,
  val eventType: EventType,
)

@Serializable
data class MapDto(
  val id: Int,
  val name: String,
  val description: String,
  val imageData: String,
)
