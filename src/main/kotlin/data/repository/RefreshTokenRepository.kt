package pl.dev.bkwiatkowski.data.repository

import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greater
import org.jetbrains.exposed.v1.jdbc.update
import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.database.DatabaseProvider
import pl.dev.bkwiatkowski.core.database.dbQuery
import pl.dev.bkwiatkowski.core.database.initTable
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.data.dao.RefreshTokenDAO
import pl.dev.bkwiatkowski.data.entity.RefreshTokenTable
import pl.dev.bkwiatkowski.domain.model.TokenVerificationResult
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.time.Duration.Companion.seconds

interface RefreshTokenRepository {
  suspend fun saveToken(userId: Int, token: String, expiresAt: LocalDateTime): Either<DomainError, Unit>
  suspend fun verifyAndRevokeToken(token: String): Either<DomainError, TokenVerificationResult>
  suspend fun revokeAllTokensForUser(userId: Int): Either<DomainError, Unit>
}

class RefreshTokenRepositoryImpl(
  databaseProvider: DatabaseProvider
) : RefreshTokenRepository {

  private val database = databaseProvider.get()
  
  init {
    either { 
      database.getRight().initTable(table = RefreshTokenTable)
    }
  }

  override suspend fun saveToken(userId: Int, token: String, expiresAt: LocalDateTime): Either<DomainError, Unit> = either {
    database.getRight().dbQuery {
      RefreshTokenDAO.new {
        this.userId = userId
        this.token = token
        this.revoked = false
        this.revokedAt = null
        this.expiresAt = expiresAt
      }
    }.getRight()
  }

  override suspend fun verifyAndRevokeToken(token: String): Either<DomainError, TokenVerificationResult> = either {
    database.getRight().dbQuery {
      val now = LocalDateTime.now()

      val tokenDao = RefreshTokenDAO.find {
        (RefreshTokenTable.token eq token) and
            (RefreshTokenTable.expiresAt greater now)
      }.singleOrNull()

      if (tokenDao != null) {
        if (tokenDao.revoked) {
          val revokedAt = tokenDao.revokedAt
          val isInsideGracePeriod = revokedAt != null && ChronoUnit.SECONDS.between(revokedAt, now) <= GRACE_PERIOD.inWholeSeconds

          TokenVerificationResult(
            userId = tokenDao.userId,
            isReused = !isInsideGracePeriod,
          )
        } else {
          tokenDao.revoked = true
          tokenDao.revokedAt = now
          TokenVerificationResult(
            userId = tokenDao.userId,
            isReused = false,
          )
        }
      } else {
        raise(
          error = DomainError.Custom(
            e = NoSuchElementException("Token not found or expired"),
          ),
        )
      }
    }.getRight()
  }

  override suspend fun revokeAllTokensForUser(userId: Int): Either<DomainError, Unit> = either {
    database.getRight().dbQuery {
      RefreshTokenTable.update(where = { RefreshTokenTable.userId eq userId }) { column ->
        column[revoked] = true
        column[revokedAt] = LocalDateTime.now()
      }
    }.getRight()
  }

  companion object {
    private val GRACE_PERIOD = 5.seconds
  }
}
