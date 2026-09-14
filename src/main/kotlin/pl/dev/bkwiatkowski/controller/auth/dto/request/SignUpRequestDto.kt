package pl.dev.bkwiatkowski.controller.auth.dto.request

import kotlinx.serialization.Serializable
import pl.dev.bkwiatkowski.domain.model.AdminPanelUser

@Serializable
data class SignUpRequestDto(
  val username: String,
  val password: String,
  val email: String,
  val role: AdminPanelUser.Role = AdminPanelUser.Role.USER,
)
