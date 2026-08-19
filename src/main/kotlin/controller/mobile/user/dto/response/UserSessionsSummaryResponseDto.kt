package pl.dev.bkwiatkowski.controller.mobile.user.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class UserSessionsSummaryResponseDto(
  val sessions: List<UserSessionSummaryDto>,
)
