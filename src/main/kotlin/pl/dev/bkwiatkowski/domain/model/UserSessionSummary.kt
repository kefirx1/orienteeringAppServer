package pl.dev.bkwiatkowski.domain.model

import java.time.LocalDateTime

data class UserSessionSummary(
  val sessionUuid: String,
  val startedAt: LocalDateTime,
  val finishedAt: LocalDateTime,
  val visitedWaypointsCount: Int,
  val mapName: String,
  val eventName: String,
)
