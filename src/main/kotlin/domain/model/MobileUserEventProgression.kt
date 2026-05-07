package pl.dev.bkwiatkowski.domain.model

import java.time.LocalDateTime

data class MobileUserEventProgression(
  val id: Int = 0,
  val userId: Int,
  val eventId: Int,
  val startedAt: LocalDateTime,
  val finishedAt: LocalDateTime?,
  val visitedWaypointsCount: Int,
  val isLiveTracking: Boolean,
)
