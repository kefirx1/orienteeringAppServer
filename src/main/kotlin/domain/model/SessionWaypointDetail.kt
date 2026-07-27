package pl.dev.bkwiatkowski.domain.model

import java.time.LocalDateTime

data class SessionWaypointDetail(
  val id: Int,
  val sessionUuid: String,
  val userId: Int,
  val waypointId: Int,
  val visitedAt: LocalDateTime,
)
