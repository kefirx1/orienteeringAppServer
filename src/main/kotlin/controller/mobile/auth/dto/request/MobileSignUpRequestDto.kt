package pl.dev.bkwiatkowski.controller.mobile.auth.dto.request

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class MobileSignUpRequestDto(
  val username: String,
  val email: String,
  val password: String,
  val phoneNumber: String? = null,
  @Contextual val dateOfBirth: LocalDateTime? = null,
)
