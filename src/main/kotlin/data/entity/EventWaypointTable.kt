package pl.dev.bkwiatkowski.data.entity

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object EventWaypointTable : IntIdTable(name = "event_waypoints") {
  val eventId = integer(name = "event_id").references(EventTable.id)
  val waypointId = integer(name = "waypoint_id").references(MapWaypointTable.id)

  init {
    uniqueIndex(eventId, waypointId)
  }
}
