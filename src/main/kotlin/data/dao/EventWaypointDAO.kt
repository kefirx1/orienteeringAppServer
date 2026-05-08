package pl.dev.bkwiatkowski.data.dao

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import pl.dev.bkwiatkowski.data.entity.EventWaypointTable

class EventWaypointDAO(id: EntityID<Int>) : IntEntity(id) {
  companion object : IntEntityClass<EventWaypointDAO>(EventWaypointTable)

  var eventId by EventWaypointTable.eventId
  var waypointId by EventWaypointTable.waypointId
}
