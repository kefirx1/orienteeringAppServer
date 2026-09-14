package domain.repository

import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.domain.model.MobileUser
import pl.dev.bkwiatkowski.domain.model.SaltedHash

interface MobileUserRepository {
  suspend fun getUserById(id: Int): Either<DomainError, MobileUser>
  suspend fun getUserByUsername(username: String): Either<DomainError, MobileUser>
  suspend fun getUserByEmail(email: String): Either<DomainError, MobileUser>
  suspend fun insertUser(user: MobileUser): Either<DomainError, Unit>
  suspend fun updateUserPassword(id: Int, saltedHash: SaltedHash): Either<DomainError, Unit>
}