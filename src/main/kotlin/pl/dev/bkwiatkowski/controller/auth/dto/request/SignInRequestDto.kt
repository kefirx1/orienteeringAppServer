package pl.dev.bkwiatkowski.controller.auth.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class SignInRequestDto(
  val username: String,
  val password: String,
)
