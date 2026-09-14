package pl.dev.bkwiatkowski.domain.usecase

import domain.repository.MobileUserRefreshTokenRepository
import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase

interface RevokeAllMobileUserRefreshTokensUC : UseCase<RevokeAllMobileUserRefreshTokensUC.Params, Unit> {
  data class Params(
    val userId: Int,
  ) : UseCase.Params
}

class RevokeAllMobileUserRefreshTokensUCImpl(
  private val mobileUserRefreshTokenRepository: MobileUserRefreshTokenRepository,
) : RevokeAllMobileUserRefreshTokensUC {
  override suspend fun invoke(params: RevokeAllMobileUserRefreshTokensUC.Params): Either<DomainError, Unit> =
    mobileUserRefreshTokenRepository.revokeAllTokensForUser(userId = params.userId)
}
