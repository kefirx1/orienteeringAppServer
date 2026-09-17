package pl.dev.bkwiatkowski.controller.mobile.events.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class SessionWaypointDetailsResponseDto(
  val sessionWaypointDetails: List<SessionWaypointDetailDto>,
  val hasLowAccuracy: Boolean,
)
