package pl.dev.bkwiatkowski.data.entity

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.javatime.datetime

object EventSessionTable : IntIdTable(name = "event_sessions") {
  val sessionUuid = varchar(name = "session_uuid", length = 36).uniqueIndex()
  val eventId = integer(name = "event_id").references(EventTable.id)
  val startedAt = datetime(name = "started_at")
  val finishedAt = datetime(name = "finished_at").nullable()
  val userCanJoin = bool(name = "user_can_join").default(true)
}
