package pl.dev.bkwiatkowski.controller.mobile.settings.dto.response

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class MobileSettingsResponseDto(
  @Contextual
  val serverLocalDateTime: LocalDateTime,
)
