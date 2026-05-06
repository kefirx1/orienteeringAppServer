package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.EnvironmentConfig
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.data.repository.MobileUserRefreshTokenRepository
import java.time.LocalDateTime

interface SaveMobileUserRefreshTokenUC : UseCase<SaveMobileUserRefreshTokenUC.Params, Unit> {
  data class Params(
    val userId: Int,
    val token: String,
  ) : UseCase.Params
}

class SaveMobileUserRefreshTokenUCImpl(
  private val mobileUserRefreshTokenRepository: MobileUserRefreshTokenRepository,
  private val environmentConfig: EnvironmentConfig,
) : SaveMobileUserRefreshTokenUC {
  override suspend fun invoke(params: SaveMobileUserRefreshTokenUC.Params): Either<DomainError, Unit> {
    val expiresAt = LocalDateTime.now().plusSeconds(environmentConfig.jwtRefreshExpiresIn.inWholeSeconds)
    return mobileUserRefreshTokenRepository.saveToken(
      userId = params.userId,
      token = params.token,
      expiresAt = expiresAt,
    )
  }
}
