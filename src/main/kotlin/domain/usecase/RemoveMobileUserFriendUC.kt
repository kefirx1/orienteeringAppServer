package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.data.repository.MobileUserFriendsRepository

interface RemoveMobileUserFriendUC : UseCase<RemoveMobileUserFriendUC.Params, Unit> {
  data class Params(
    val userId: Int,
    val friendId: Int,
  ) : UseCase.Params
}

class RemoveMobileUserFriendUCImpl(
  private val mobileUserFriendsRepository: MobileUserFriendsRepository,
) : RemoveMobileUserFriendUC {
  override suspend fun invoke(params: RemoveMobileUserFriendUC.Params): Either<DomainError, Unit> =
    mobileUserFriendsRepository.removeFriend(
      userId = params.userId,
      friendId = params.friendId,
    )
}
