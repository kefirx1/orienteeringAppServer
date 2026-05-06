package pl.dev.bkwiatkowski.controller.auth.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordRequestDto(
  val oldPassword: String,
  val newPassword: String,
)
