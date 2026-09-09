package pl.dev.bkwiatkowski.controller.mobile.user.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class SendFriendRequestDto(
  val friendId: Int,
)