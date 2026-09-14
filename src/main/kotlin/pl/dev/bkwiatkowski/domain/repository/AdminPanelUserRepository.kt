package pl.dev.bkwiatkowski.domain.repository

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.domain.model.AdminPanelUser
import pl.dev.bkwiatkowski.domain.model.SaltedHash

interface AdminPanelUserRepository {
  suspend fun getAllUsers(): Either<DomainError, List<AdminPanelUser>>
  suspend fun getUserById(id: Int): Either<DomainError, AdminPanelUser>
  suspend fun getUserByUsername(username: String): Either<DomainError, AdminPanelUser>
  suspend fun getUserByEmail(email: String): Either<DomainError, AdminPanelUser>
  suspend fun insertUser(user: AdminPanelUser): Either<DomainError, Unit>
  suspend fun deleteUserById(id: Int): Either<DomainError, Unit>
  suspend fun updateUserPassword(id: Int, saltedHash: SaltedHash): Either<DomainError, Unit>
}