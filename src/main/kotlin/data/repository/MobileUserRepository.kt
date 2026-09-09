package pl.dev.bkwiatkowski.data.repository

import org.jetbrains.exposed.v1.core.eq
import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.database.DatabaseProvider
import pl.dev.bkwiatkowski.core.database.dbQuery
import pl.dev.bkwiatkowski.core.database.initTable
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.data.dao.MobileUserDAO
import pl.dev.bkwiatkowski.data.entity.MobileUserTable
import pl.dev.bkwiatkowski.data.mapper.toDomain
import pl.dev.bkwiatkowski.domain.model.MobileUser
import pl.dev.bkwiatkowski.domain.model.SaltedHash

interface MobileUserRepository {
  suspend fun getUserById(id: Int): Either<DomainError, MobileUser>
  suspend fun getUserByUsername(username: String): Either<DomainError, MobileUser>
  suspend fun getUserByEmail(email: String): Either<DomainError, MobileUser>
  suspend fun insertUser(user: MobileUser): Either<DomainError, Unit>
  suspend fun updateUserPassword(id: Int, saltedHash: SaltedHash): Either<DomainError, Unit>
}

class MobileUserRepositoryImpl(
  databaseProvider: DatabaseProvider,
) : MobileUserRepository {

  private val database = databaseProvider.get()

  init {
    either {
      database.getRight().initTable(table = MobileUserTable)
    }
  }

  override suspend fun getUserById(id: Int): Either<DomainError, MobileUser> = either {
    database.getRight().dbQuery {
      MobileUserDAO.findById(id)?.toDomain()
        ?: raise(error = DomainError.Custom(e = NullPointerException("Mobile user not found")))
    }.getRight()
  }

  override suspend fun getUserByUsername(username: String): Either<DomainError, MobileUser> = either {
    database.getRight().dbQuery {
      MobileUserDAO.find { MobileUserTable.username eq username }.singleOrNull()?.toDomain()
        ?: raise(error = DomainError.Custom(e = NullPointerException("Mobile user not found")))
    }.getRight()
  }

  override suspend fun getUserByEmail(email: String): Either<DomainError, MobileUser> = either {
    database.getRight().dbQuery {
      MobileUserDAO.find { MobileUserTable.email eq email }.singleOrNull()?.toDomain()
        ?: raise(error = DomainError.Custom(e = NullPointerException("Mobile user not found")))
    }.getRight()
  }

  override suspend fun insertUser(user: MobileUser): Either<DomainError, Unit> = either {
    database.getRight().dbQuery {
      MobileUserDAO.new {
        username = user.username
        email = user.email
        password = user.password
        salt = user.salt
        phoneNumber = user.phoneNumber
        dateOfBirth = user.dateOfBirth
        joinedAt = user.joinedAt
      }
    }.getRight()
  }

  override suspend fun updateUserPassword(id: Int, saltedHash: SaltedHash): Either<DomainError, Unit> = either {
    database.getRight().dbQuery {
      val user = MobileUserDAO.findById(id)
        ?: raise(error = DomainError.Custom(e = NullPointerException("Mobile user not found")))
      user.password = saltedHash.hash
      user.salt = saltedHash.salt
    }.getRight()
  }
}
