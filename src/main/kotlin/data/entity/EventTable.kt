package pl.dev.bkwiatkowski.data.entity

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.javatime.datetime

object EventTable : IntIdTable(name = "events") {
  val mapId = integer(name = "map_id").references(MapTable.id)
  val userId = integer(name = "user_id")
  val name = varchar(name = "name", length = 255)
  val description = varchar(name = "description", length = 1000)
  val createdAt = datetime(name = "created_at")
  val startDate = datetime(name = "start_date")
  val startLocationX = float(name = "start_location_x")
  val startLocationY = float(name = "start_location_y")
}
