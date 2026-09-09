package pl.dev.bkwiatkowski.controller.mobile.user.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class RemoveFriendDto(
  val friendId: Int,
)