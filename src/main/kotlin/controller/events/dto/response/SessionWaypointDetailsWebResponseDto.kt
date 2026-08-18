package pl.dev.bkwiatkowski.controller.events.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class SessionWaypointDetailsWebResponseDto(
  val sessionWaypointDetails: List<SessionWaypointDetailWebDto>,
)
