package pl.dev.bkwiatkowski.controller.mobile.user.dto.response

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class UserSessionSummaryDto(
  val sessionUuid: String,
  @Contextual val startedAt: LocalDateTime,
  @Contextual val finishedAt: LocalDateTime,
  val visitedWaypointsCount: Int,
  val mapName: String,
  val eventName: String,
)
