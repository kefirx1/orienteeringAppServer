package pl.dev.bkwiatkowski.data.entity

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object MapTable : IntIdTable(name = "maps") {
  val name = varchar(name = "name", length = 255)
  val description = varchar(name = "description", length = 1000)
  val imageData = text(name = "image_data")
  val canPlayManually = bool(name = "can_play_manually")
}
