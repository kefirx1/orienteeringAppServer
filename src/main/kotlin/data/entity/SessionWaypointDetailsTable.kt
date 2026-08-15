package pl.dev.bkwiatkowski.data.entity

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.javatime.datetime

object SessionWaypointDetailsTable : IntIdTable(name = "session_waypoint_details") {
  val sessionUuid = varchar(name = "session_uuid", length = 36).references(EventSessionTable.sessionUuid)
  val userId = integer(name = "user_id").references(MobileUserTable.id)
  val waypointId = integer(name = "waypoint_id").references(MapWaypointTable.id)
  val visitedAt = datetime(name = "visited_at")
  val imagePath = varchar(name = "image_path", length = 512)
}
