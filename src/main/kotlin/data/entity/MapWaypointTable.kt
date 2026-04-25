package pl.dev.bkwiatkowski.data.entity

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object MapWaypointTable : IntIdTable(name = "map_waypoints") {
  val mapId = integer(name = "map_id").references(MapTable.id)
  val label = varchar(name = "label", length = 255)
  val coordinateX = float(name = "coordinate_x")
  val coordinateY = float(name = "coordinate_y")
}
