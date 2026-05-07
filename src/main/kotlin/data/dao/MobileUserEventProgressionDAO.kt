package pl.dev.bkwiatkowski.data.dao

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import pl.dev.bkwiatkowski.data.entity.MobileUserEventProgressionTable

class MobileUserEventProgressionDAO(id: EntityID<Int>) : IntEntity(id) {
  companion object : IntEntityClass<MobileUserEventProgressionDAO>(MobileUserEventProgressionTable)

  var userId by MobileUserEventProgressionTable.userId
  var eventId by MobileUserEventProgressionTable.eventId
  var startedAt by MobileUserEventProgressionTable.startedAt
  var finishedAt by MobileUserEventProgressionTable.finishedAt
  var visitedWaypointsCount by MobileUserEventProgressionTable.visitedWaypointsCount
  var isLiveTracking by MobileUserEventProgressionTable.isLiveTracking
}
