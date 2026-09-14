package pl.dev.bkwiatkowski.data.dao

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import pl.dev.bkwiatkowski.data.entity.RefreshTokenTable

class RefreshTokenDAO(id: EntityID<Int>) : IntEntity(id) {
  companion object : IntEntityClass<RefreshTokenDAO>(RefreshTokenTable)

  var userId by RefreshTokenTable.userId
  var token by RefreshTokenTable.token
  var revoked by RefreshTokenTable.revoked
  var revokedAt by RefreshTokenTable.revokedAt
  var expiresAt by RefreshTokenTable.expiresAt
}
