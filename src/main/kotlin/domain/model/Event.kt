package pl.dev.bkwiatkowski.domain.model

import java.time.LocalDateTime

data class Event(
  val id: Int = 0,
  val map: MapData,
  val userId: Int,
  val name: String,
  val description: String,
  val createdAt: LocalDateTime,
  val startDate: LocalDateTime,
  val startLocationX: Float,
  val startLocationY: Float,
  val status: EventStatus,
  val finishedAt: LocalDateTime? = null,
  val allowOfflineTracking: Boolean,
  val eventType: EventType,
)
