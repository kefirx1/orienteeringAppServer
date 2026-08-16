package pl.dev.bkwiatkowski.controller.mobile.events.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class FinishSessionResponseDto(
  val participant: SessionParticipantDto,
  val sessionWaypointDetails: List<SessionWaypointDetailDto>,
)
