package pl.dev.bkwiatkowski.controller.auth.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class SignInResponseDto(
  val token: String,
)
