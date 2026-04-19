package pl.dev.bkwiatkowski.data.mapper

import pl.dev.bkwiatkowski.data.dao.AdminPanelUserDAO
import pl.dev.bkwiatkowski.domain.model.AdminPanelUser

fun AdminPanelUserDAO.toDomain() = AdminPanelUser(
  id = id.value,
  username = username,
  email = email,
  password = password,
  salt = salt,
  role = role
)