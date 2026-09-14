package domain.repository

import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.domain.model.MobileUserFriend

interface MobileUserFriendsRepository {
  suspend fun getFriendsList(userId: Int): Either<DomainError, List<MobileUserFriend>>
  suspend fun sendFriendRequest(userId: Int, friendId: Int): Either<DomainError, Unit>
  suspend fun acceptFriendRequest(userId: Int, friendId: Int): Either<DomainError, Unit>
  suspend fun removeFriend(userId: Int, friendId: Int): Either<DomainError, Unit>
  suspend fun getFriendshipStatus(userId: Int, friendId: Int): Either<DomainError, MobileUserFriend?>
}