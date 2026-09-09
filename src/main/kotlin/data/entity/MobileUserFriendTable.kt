package pl.dev.bkwiatkowski.data.entity

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.javatime.datetime

object MobileUserFriendTable : IntIdTable(name = "mobile_user_friends") {
  val userId = integer(name = "user_id").references(MobileUserTable.id)
  val friendId = integer(name = "friend_id").references(MobileUserTable.id)
  val createdAt = datetime(name = "created_at")
  val status = varchar(name = "status", length = 20)
}