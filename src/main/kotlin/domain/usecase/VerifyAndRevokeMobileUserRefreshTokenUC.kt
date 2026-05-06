package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.data.repository.MobileUserRefreshTokenRepository
import pl.dev.bkwiatkowski.domain.model.TokenVerificationResult

interface VerifyAndRevokeMobileUserRefreshTokenUC :
  UseCase<VerifyAndRevokeMobileUserRefreshTokenUC.Params, TokenVerificationResult> {
  data class Params(
    val token: String,
  ) : UseCase.Params
}

class VerifyAndRevokeMobileUserRefreshTokenUCImpl(
  private val mobileUserRefreshTokenRepository: MobileUserRefreshTokenRepository,
) : VerifyAndRevokeMobileUserRefreshTokenUC {
  override suspend fun invoke(
    params: VerifyAndRevokeMobileUserRefreshTokenUC.Params,
  ): Either<DomainError, TokenVerificationResult> =
    mobileUserRefreshTokenRepository.verifyAndRevokeToken(token = params.token)
}
