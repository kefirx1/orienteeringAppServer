package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.data.repository.MobileUserRepository
import pl.dev.bkwiatkowski.domain.model.MobileUser

interface GetMobileUserByIdUC : UseCase<GetMobileUserByIdUC.Params, MobileUser> {
  data class Params(
    val id: Int,
  ) : UseCase.Params
}

class GetMobileUserByIdUCImpl(
  private val mobileUserRepository: MobileUserRepository,
) : GetMobileUserByIdUC {
  override suspend fun invoke(params: GetMobileUserByIdUC.Params): Either<DomainError, MobileUser> =
    mobileUserRepository.getUserById(id = params.id)
}
