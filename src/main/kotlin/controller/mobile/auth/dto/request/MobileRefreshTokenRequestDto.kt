package pl.dev.bkwiatkowski.controller.mobile.auth.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class MobileRefreshTokenRequestDto(
  val refreshToken: String,
)
