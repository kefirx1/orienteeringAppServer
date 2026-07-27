package pl.dev.bkwiatkowski.data.entity

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.javatime.datetime

object SessionParticipantsTable : IntIdTable(name = "session_participants") {
  val sessionUuid = varchar(name = "session_uuid", length = 36).references(EventSessionTable.sessionUuid)
  val userId = integer(name = "user_id").references(MobileUserTable.id)
  val joinedAt = datetime(name = "joined_at")
  val finishedAt = datetime(name = "finished_at").nullable()
}
