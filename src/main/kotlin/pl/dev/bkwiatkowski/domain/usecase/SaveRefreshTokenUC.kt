package pl.dev.bkwiatkowski.domain.usecase

import domain.repository.RefreshTokenRepository
import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.EnvironmentConfig
import pl.dev.bkwiatkowski.core.UseCase
import java.time.LocalDateTime

interface SaveRefreshTokenUC : UseCase<SaveRefreshTokenUC.Params, Unit> {
  data class Params(
    val userId: Int,
    val token: String,
  ): UseCase.Params
}

class SaveRefreshTokenUCImpl(
  private val refreshTokenRepository: RefreshTokenRepository,
  private val environmentConfig: EnvironmentConfig
) : SaveRefreshTokenUC {
  override suspend fun invoke(params: SaveRefreshTokenUC.Params): Either<DomainError, Unit> {
    val expiresAt = LocalDateTime.now().plusSeconds(environmentConfig.jwtRefreshExpiresIn.inWholeSeconds)
    
    return refreshTokenRepository.saveToken(
      userId = params.userId,
      token = params.token,
      expiresAt = expiresAt
    )
  }
}
