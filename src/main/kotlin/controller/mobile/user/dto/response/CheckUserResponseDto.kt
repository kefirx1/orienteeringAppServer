package pl.dev.bkwiatkowski.controller.mobile.user.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class CheckUserResponseDto(
  val username: String,
)
