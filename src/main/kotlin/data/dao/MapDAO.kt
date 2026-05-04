package pl.dev.bkwiatkowski.data.dao

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import pl.dev.bkwiatkowski.data.entity.MapTable

class MapDAO(id: EntityID<Int>) : IntEntity(id) {
  companion object : IntEntityClass<MapDAO>(MapTable)

  var name by MapTable.name
  var description by MapTable.description
  var imageData by MapTable.imageData
  var canPlayManually by MapTable.canPlayManually
}
