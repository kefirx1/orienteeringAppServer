package pl.dev.bkwiatkowski.domain.model

import java.time.LocalDateTime

data class SessionParticipant(
  val id: Int,
  val sessionUuid: String,
  val userId: Int,
  val joinedAt: LocalDateTime,
  val finishedAt: LocalDateTime? = null,
)
