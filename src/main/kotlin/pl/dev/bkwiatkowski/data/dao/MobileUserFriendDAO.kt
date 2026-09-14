package pl.dev.bkwiatkowski.data.dao

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import pl.dev.bkwiatkowski.data.entity.MobileUserFriendTable

class MobileUserFriendDAO(id: EntityID<Int>) : IntEntity(id) {
  companion object : IntEntityClass<MobileUserFriendDAO>(MobileUserFriendTable)

  var userId by MobileUserFriendTable.userId
  var friendId by MobileUserFriendTable.friendId
  var createdAt by MobileUserFriendTable.createdAt
  var status by MobileUserFriendTable.status
}
