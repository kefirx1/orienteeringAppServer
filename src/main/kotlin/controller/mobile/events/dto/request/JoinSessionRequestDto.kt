package pl.dev.bkwiatkowski.controller.mobile.events.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class JoinSessionRequestDto(
  val sessionUuid: String,
)
