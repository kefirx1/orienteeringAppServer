package pl.dev.bkwiatkowski.controller.adminusers.dto.request

import kotlinx.serialization.Serializable
import pl.dev.bkwiatkowski.domain.model.AdminPanelUser

@Serializable
data class AddAdminUserRequestDto(
  val username: String,
  val password: String,
  val email: String,
  val role: AdminPanelUser.Role = AdminPanelUser.Role.USER,
)
