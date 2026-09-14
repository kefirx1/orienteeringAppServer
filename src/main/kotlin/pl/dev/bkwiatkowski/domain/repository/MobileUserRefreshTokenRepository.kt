package domain.repository

import java.time.LocalDateTime
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.domain.model.TokenVerificationResult

interface MobileUserRefreshTokenRepository {
  suspend fun saveToken(userId: Int, token: String, expiresAt: LocalDateTime): Either<DomainError, Unit>
  suspend fun verifyAndRevokeToken(token: String): Either<DomainError, TokenVerificationResult>
  suspend fun revokeAllTokensForUser(userId: Int): Either<DomainError, Unit>
}