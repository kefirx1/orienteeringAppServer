package pl.dev.bkwiatkowski.data.entity

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.javatime.date

object MobileUserTable : IntIdTable(name = "mobile_users") {
  val username = varchar(name = "username", length = 255).uniqueIndex()
  val email = varchar(name = "email", length = 255).uniqueIndex()
  val password = varchar(name = "password", length = 255)
  val salt = varchar(name = "salt", length = 255)
  val phoneNumber = varchar(name = "phone_number", length = 32).nullable()
  val dateOfBirth = date(name = "date_of_birth").nullable()
}
