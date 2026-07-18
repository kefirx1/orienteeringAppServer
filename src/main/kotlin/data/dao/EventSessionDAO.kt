package pl.dev.bkwiatkowski.data.dao

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import pl.dev.bkwiatkowski.data.entity.EventSessionTable

class EventSessionDAO(id: EntityID<Int>) : IntEntity(id) {
  companion object : IntEntityClass<EventSessionDAO>(EventSessionTable)

  var sessionUuid by EventSessionTable.sessionUuid
  var eventId by EventSessionTable.eventId
  var startedAt by EventSessionTable.startedAt
}
