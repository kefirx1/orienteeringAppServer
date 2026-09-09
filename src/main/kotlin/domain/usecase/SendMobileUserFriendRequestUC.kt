package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.data.repository.MobileUserFriendsRepository
import pl.dev.bkwiatkowski.data.repository.MobileUserRepository

interface SendMobileUserFriendRequestUC : UseCase<SendMobileUserFriendRequestUC.Params, Unit> {
  data class Params(
    val userId: Int,
    val friendId: Int,
  ) : UseCase.Params
}

class SendMobileUserFriendRequestUCImpl(
  private val mobileUserFriendsRepository: MobileUserFriendsRepository,
  private val mobileUserRepository: MobileUserRepository,
) : SendMobileUserFriendRequestUC {
  override suspend fun invoke(params: SendMobileUserFriendRequestUC.Params): Either<DomainError, Unit> = either {
    mobileUserRepository.getUserById(id = params.friendId).onRight {
      mobileUserFriendsRepository.sendFriendRequest(
        userId = params.userId,
        friendId = params.friendId,
      ).getRight()
    }.getRight()
  }
}
