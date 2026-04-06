package pl.dev.bkwiatkowski.controller.auth.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequestDto(
  val username: String,
  val password: String,
  val email: String,
)