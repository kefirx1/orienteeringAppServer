package pl.dev.bkwiatkowski.controller.mobile.user.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class GetFriendsListResponseDto(
  val friends: List<FriendDto>,
)