package pl.dev.bkwiatkowski.controller.auth.dto.response

import kotlinx.serialization.Serializable
import pl.dev.bkwiatkowski.domain.model.AdminPanelUser

@Serializable
data class SignInResponseDto(
  val token: String,
  val expiresInSec: Long,
  val role: AdminPanelUser.Role,
)
