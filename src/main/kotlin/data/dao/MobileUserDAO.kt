package pl.dev.bkwiatkowski.data.dao

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import pl.dev.bkwiatkowski.data.entity.MobileUserTable

class MobileUserDAO(id: EntityID<Int>) : IntEntity(id) {
  companion object : IntEntityClass<MobileUserDAO>(MobileUserTable)

  var username by MobileUserTable.username
  var email by MobileUserTable.email
  var password by MobileUserTable.password
  var salt by MobileUserTable.salt
  var phoneNumber by MobileUserTable.phoneNumber
  var dateOfBirth by MobileUserTable.dateOfBirth
}
