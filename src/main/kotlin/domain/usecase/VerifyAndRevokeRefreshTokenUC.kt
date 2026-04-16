package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.data.repository.RefreshTokenRepository
import pl.dev.bkwiatkowski.domain.model.TokenVerificationResult

interface VerifyAndRevokeRefreshTokenUC : UseCase<VerifyAndRevokeRefreshTokenUC.Params, TokenVerificationResult> {
  data class Params(
    val token: String,
  ) : UseCase.Params
}

class VerifyAndRevokeRefreshTokenUCImpl(
  private val refreshTokenRepository: RefreshTokenRepository
) : VerifyAndRevokeRefreshTokenUC {
  override suspend fun invoke(params: VerifyAndRevokeRefreshTokenUC.Params): Either<DomainError, TokenVerificationResult> =
    refreshTokenRepository.verifyAndRevokeToken(token = params.token)
}
