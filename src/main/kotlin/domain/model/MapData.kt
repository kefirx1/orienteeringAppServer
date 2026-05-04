package pl.dev.bkwiatkowski.domain.model

data class MapData(
  val id: Int = 0,
  val name: String,
  val description: String,
  val imageData: String,
  val canPlayManually: Boolean,
  val mapWaypoints: List<MapWaypoint>,
)
