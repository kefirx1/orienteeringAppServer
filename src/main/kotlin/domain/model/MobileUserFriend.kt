package pl.dev.bkwiatkowski.domain.model

import java.time.LocalDateTime

enum class FriendshipStatus {
  ACCEPTED,
  NOT_ACCEPTED,
}

data class MobileUserFriend(
  val id: Int = 0,
  val userId: Int,
  val friendId: Int,
  val createdAt: LocalDateTime,
  val status: FriendshipStatus,
)
