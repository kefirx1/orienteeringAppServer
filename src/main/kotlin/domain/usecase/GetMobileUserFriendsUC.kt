package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.data.repository.MobileUserFriendsRepository
import pl.dev.bkwiatkowski.domain.model.MobileUserFriend

interface GetMobileUserFriendsUC : UseCase<GetMobileUserFriendsUC.Params, List<MobileUserFriend>> {
  data class Params(
    val userId: Int,
  ) : UseCase.Params
}

class GetMobileUserFriendsUCImpl(
  private val mobileUserFriendsRepository: MobileUserFriendsRepository,
) : GetMobileUserFriendsUC {
  override suspend fun invoke(params: GetMobileUserFriendsUC.Params): Either<DomainError, List<MobileUserFriend>> =
    mobileUserFriendsRepository.getFriendsList(userId = params.userId)
}
