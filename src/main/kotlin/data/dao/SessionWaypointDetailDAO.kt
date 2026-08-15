package pl.dev.bkwiatkowski.data.dao

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import pl.dev.bkwiatkowski.data.entity.SessionWaypointDetailsTable

class SessionWaypointDetailDAO(id: EntityID<Int>) : IntEntity(id) {
  companion object : IntEntityClass<SessionWaypointDetailDAO>(SessionWaypointDetailsTable)

  var sessionUuid by SessionWaypointDetailsTable.sessionUuid
  var userId by SessionWaypointDetailsTable.userId
  var waypointId by SessionWaypointDetailsTable.waypointId
  var visitedAt by SessionWaypointDetailsTable.visitedAt
  var imagePath by SessionWaypointDetailsTable.imagePath
}
