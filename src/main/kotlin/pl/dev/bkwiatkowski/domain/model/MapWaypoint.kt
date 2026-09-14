package pl.dev.bkwiatkowski.domain.model

data class MapWaypoint(
  val id: Int = 0,
  val label: String,
  val coordinateX: Float,
  val coordinateY: Float,
)
