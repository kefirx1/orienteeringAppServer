package pl.dev.bkwiatkowski.data.entity

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object AdminPanelUserTable : IntIdTable(name = "admin_panel_users") {
  val username = varchar(name = "username", length = 255).uniqueIndex()
  val email = varchar(name = "email", length = 255).uniqueIndex()
  val password = varchar(name = "password", length = 255)
  val salt = varchar(name = "salt", length = 255)
}
