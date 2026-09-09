package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.data.repository.MobileUserFriendsRepository
import pl.dev.bkwiatkowski.domain.model.MobileUserFriend

interface GetFriendshipStatusUC : UseCase<GetFriendshipStatusUC.Params, MobileUserFriend?> {
  data class Params(
    val userId: Int,
    val friendId: Int,
  ) : UseCase.Params
}

class GetFriendshipStatusUCImpl(
  private val mobileUserFriendsRepository: MobileUserFriendsRepository,
) : GetFriendshipStatusUC {
  override suspend fun invoke(params: GetFriendshipStatusUC.Params): Either<DomainError, MobileUserFriend?> =
    mobileUserFriendsRepository.getFriendshipStatus(
      userId = params.userId,
      friendId = params.friendId,
    )
}
