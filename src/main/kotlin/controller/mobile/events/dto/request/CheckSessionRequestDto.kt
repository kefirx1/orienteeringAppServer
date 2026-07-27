package pl.dev.bkwiatkowski.controller.mobile.events.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CheckSessionRequestDto(
  val sessionUuid: String,
)
