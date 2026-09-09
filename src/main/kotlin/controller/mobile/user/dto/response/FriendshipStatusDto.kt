package pl.dev.bkwiatkowski.controller.mobile.user.dto.response

import kotlinx.serialization.Serializable

@Serializable
enum class FriendshipStatusDto {
  ACCEPTED,
  NOT_ACCEPTED,
}
