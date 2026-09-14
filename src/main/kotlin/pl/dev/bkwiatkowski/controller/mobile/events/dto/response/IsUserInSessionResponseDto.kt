package pl.dev.bkwiatkowski.controller.mobile.events.dto.response

import kotlinx.serialization.Serializable

@Serializable
enum class JoinStatus {
  JOINED,
  NOT_JOINED,
  FINISHED,
}

@Serializable
data class IsUserInSessionResponseDto(
  val status: JoinStatus,
)
