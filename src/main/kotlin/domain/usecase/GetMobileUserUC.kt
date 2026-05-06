package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.data.repository.MobileUserRepository
import pl.dev.bkwiatkowski.domain.model.MobileUser

interface GetMobileUserUC : UseCase<GetMobileUserUC.Params, MobileUser> {
  data class Params(
    val username: String,
  ) : UseCase.Params
}

class GetMobileUserUCImpl(
  private val mobileUserRepository: MobileUserRepository,
) : GetMobileUserUC {
  override suspend fun invoke(params: GetMobileUserUC.Params): Either<DomainError, MobileUser> =
    mobileUserRepository.getUserByUsername(username = params.username)
}
