package pl.dev.bkwiatkowski.controller.maps.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class MapListResponseDto(
  val id: Int,
  val name: String,
  val description: String,
  val imageUri: String,
)
