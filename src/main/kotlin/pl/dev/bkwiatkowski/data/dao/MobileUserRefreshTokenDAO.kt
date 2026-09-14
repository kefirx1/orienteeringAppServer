package pl.dev.bkwiatkowski.data.dao

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import pl.dev.bkwiatkowski.data.entity.MobileUserRefreshTokenTable

class MobileUserRefreshTokenDAO(id: EntityID<Int>) : IntEntity(id) {
  companion object : IntEntityClass<MobileUserRefreshTokenDAO>(MobileUserRefreshTokenTable)

  var userId by MobileUserRefreshTokenTable.userId
  var token by MobileUserRefreshTokenTable.token
  var revoked by MobileUserRefreshTokenTable.revoked
  var revokedAt by MobileUserRefreshTokenTable.revokedAt
  var expiresAt by MobileUserRefreshTokenTable.expiresAt
}
