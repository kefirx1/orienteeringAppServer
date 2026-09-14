package pl.dev.bkwiatkowski.controller.settings.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class SettingsResponseDto(
  val username: String,
)

