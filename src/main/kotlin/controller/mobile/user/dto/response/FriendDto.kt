package pl.dev.bkwiatkowski.controller.mobile.user.dto.response

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class FriendDto(
  val friendId: Int,
  val username: String,
  @Contextual val createdAt: LocalDateTime,
  val status: FriendshipStatusDto,
  val friendStatus: FriendshipStatusDto,
)