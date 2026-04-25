package pl.dev.bkwiatkowski.controller.maps.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class MapDetailResponseDto(
  val id: Int,
  val name: String,
  val description: String,
  val imageUri: String,
  val waypoints: List<WaypointResponseDto>,
)

@Serializable
data class WaypointResponseDto(
  val id: Int,
  val label: String,
  val coordinateX: Float,
  val coordinateY: Float,
)
