package pl.dev.bkwiatkowski.controller.mobile.events.dto.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.Contextual
import pl.dev.bkwiatkowski.domain.model.EventStatus
import pl.dev.bkwiatkowski.domain.model.EventType
import java.time.LocalDateTime

@Serializable
data class MobileEventDetailResponseDto(
  val id: Int,
  val name: String,
  val description: String,
  @Contextual
  val createdAt: LocalDateTime,
  @Contextual
  val startDate: LocalDateTime,
  val startLocationX: Float,
  val startLocationY: Float,
  val status: EventStatus,
  @Contextual
  val finishedAt: LocalDateTime? = null,
  val allowOfflineTracking: Boolean,
  val eventType: EventType,
  val session: EventSessionResponseDto? = null,
  val map: MobileMapDto,
)
