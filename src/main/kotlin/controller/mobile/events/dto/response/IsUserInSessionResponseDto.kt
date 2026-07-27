package pl.dev.bkwiatkowski.controller.mobile.events.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class IsUserInSessionResponseDto(
  val joined: Boolean,
)
