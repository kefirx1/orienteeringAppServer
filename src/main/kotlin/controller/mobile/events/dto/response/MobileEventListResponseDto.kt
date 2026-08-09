package pl.dev.bkwiatkowski.controller.mobile.events.dto.response

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import pl.dev.bkwiatkowski.controller.maps.dto.response.WaypointResponseDto
import pl.dev.bkwiatkowski.domain.model.EventType
import java.time.LocalDateTime

@Serializable
data class MobileEventListResponseDto(
  val id: Int,
  val map: MobileMapDto,
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
  val eventWaypoints: List<WaypointResponseDto>,
)

@Serializable
data class MobileMapDto(
  val id: Int,
  val name: String,
  val description: String,
  val imageData: String,
  val mapWaypoints: List<WaypointResponseDto>,
)
