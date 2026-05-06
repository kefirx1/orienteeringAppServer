package pl.dev.bkwiatkowski.data.entity

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.javatime.datetime

object MobileUserRefreshTokenTable : IntIdTable(name = "mobile_user_refresh_tokens") {
  val userId = integer(name = "user_id").references(MobileUserTable.id)
  val token = varchar(name = "token", length = 1024).uniqueIndex()
  val revoked = bool(name = "revoked").default(defaultValue = false)
  val revokedAt = datetime(name = "revoked_at").nullable()
  val expiresAt = datetime(name = "expires_at")
}
