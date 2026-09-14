package pl.dev.bkwiatkowski.controller.adminusers.dto.response

import kotlinx.serialization.Serializable
import pl.dev.bkwiatkowski.domain.model.AdminPanelUser

@Serializable
data class AdminUserResponseDto(
  val id: Int,
  val username: String,
  val email: String,
  val role: AdminPanelUser.Role,
)

fun AdminPanelUser.toResponseDto() = AdminUserResponseDto(
  id = id,
  username = username,
  email = email,
  role = role,
)
