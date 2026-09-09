package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.data.repository.MobileUserFriendsRepository

interface AcceptMobileUserFriendRequestUC : UseCase<AcceptMobileUserFriendRequestUC.Params, Unit> {
  data class Params(
    val userId: Int,
    val friendId: Int,
  ) : UseCase.Params
}

class AcceptMobileUserFriendRequestUCImpl(
  private val mobileUserFriendsRepository: MobileUserFriendsRepository,
) : AcceptMobileUserFriendRequestUC {
  override suspend fun invoke(params: AcceptMobileUserFriendRequestUC.Params): Either<DomainError, Unit> =
    mobileUserFriendsRepository.acceptFriendRequest(
      userId = params.userId,
      friendId = params.friendId,
    )
}
