package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.data.repository.RefreshTokenRepository

interface RevokeAllUserRefreshTokensUC : UseCase<RevokeAllUserRefreshTokensUC.Params, Unit> {
  data class Params(
    val userId: Int,
  ): UseCase.Params
}

class RevokeAllUserRefreshTokensUCImpl(
  private val refreshTokenRepository: RefreshTokenRepository
) : RevokeAllUserRefreshTokensUC {
  override suspend fun invoke(params: RevokeAllUserRefreshTokensUC.Params): Either<DomainError, Unit> {
    return refreshTokenRepository.revokeAllTokensForUser(userId = params.userId)
  }
}
