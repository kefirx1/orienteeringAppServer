package pl.dev.bkwiatkowski.data.dao

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import pl.dev.bkwiatkowski.data.entity.AdminPanelUserTable

class AdminPanelUserDAO(id: EntityID<Int>) : IntEntity(id) {
  companion object : IntEntityClass<AdminPanelUserDAO>(AdminPanelUserTable)

  var username by AdminPanelUserTable.username
  var email by AdminPanelUserTable.email
  var password by AdminPanelUserTable.password
  var salt by AdminPanelUserTable.salt
}