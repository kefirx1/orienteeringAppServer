package pl.dev.bkwiatkowski.controller.maps.dto.request

import kotlinx.serialization.Serializable
import pl.dev.bkwiatkowski.controller.maps.dto.request.WaypointRequestDto

@Serializable
data class AddMapRequestDto(
  val name: String,
  val description: String,
  val imageUri: String,
  val waypoints: List<WaypointRequestDto> = emptyList(),
)

@Serializable
data class WaypointRequestDto(
  val label: String,
  val coordinateX: Float,
  val coordinateY: Float,
)
