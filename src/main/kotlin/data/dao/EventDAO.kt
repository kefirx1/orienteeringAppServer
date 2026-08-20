package pl.dev.bkwiatkowski.data.dao

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import pl.dev.bkwiatkowski.data.entity.EventTable

class EventDAO(id: EntityID<Int>) : IntEntity(id) {
  companion object : IntEntityClass<EventDAO>(EventTable)

  var mapId by EventTable.mapId
  var map by MapDAO referencedOn EventTable.mapId
  var userId by EventTable.userId
  var name by EventTable.name
  var description by EventTable.description
  var createdAt by EventTable.createdAt
  var startDate by EventTable.startDate
  var startLocationX by EventTable.startLocationX
  var startLocationY by EventTable.startLocationY
  var status by EventTable.status
  var eventType by EventTable.eventType
}
