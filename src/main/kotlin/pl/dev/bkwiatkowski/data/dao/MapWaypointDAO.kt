package pl.dev.bkwiatkowski.data.dao

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import pl.dev.bkwiatkowski.data.entity.MapWaypointTable

class MapWaypointDAO(id: EntityID<Int>) : IntEntity(id) {
  companion object : IntEntityClass<MapWaypointDAO>(MapWaypointTable)

  var mapId by MapWaypointTable.mapId
  var label by MapWaypointTable.label
  var coordinateX by MapWaypointTable.coordinateX
  var coordinateY by MapWaypointTable.coordinateY
}
