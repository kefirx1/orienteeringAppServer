package pl.dev.bkwiatkowski.controller.mobile.auth.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class MobileSignInResponseDto(
  val accessToken: String,
  val refreshToken: String,
  val accessTokenExpiresTimestamp: Long,
  val refreshTokenExpiresTimestamp: Long,
)
