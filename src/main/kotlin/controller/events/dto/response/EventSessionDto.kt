package pl.dev.bkwiatkowski.controller.events.dto.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.Contextual
import java.time.LocalDateTime

@Serializable
data class EventSessionDto(
  val id: String,
  @Contextual
  val startedAt: LocalDateTime,
  @Contextual
  val finishedAt: LocalDateTime? = null,
  val userCanJoin: Boolean = true,
)
