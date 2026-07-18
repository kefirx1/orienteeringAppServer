package pl.dev.bkwiatkowski.domain.model

import java.time.LocalDateTime

data class EventSession(
  val id: String,
  val eventId: Int,
  val startedAt: LocalDateTime,
)
