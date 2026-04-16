package pl.dev.bkwiatkowski.data.repository

import org.jetbrains.exposed.v1.core.eq
import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.database.DatabaseProvider
import pl.dev.bkwiatkowski.core.database.dbQuery
import pl.dev.bkwiatkowski.core.database.initTable
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.data.dao.AdminPanelUserDAO
import pl.dev.bkwiatkowski.data.entity.AdminPanelUserTable
import pl.dev.bkwiatkowski.data.mapper.toDomain
import pl.dev.bkwiatkowski.domain.model.AdminPanelUser

interface AdminPanelUserRepository {
  suspend fun getUserByUsername(username: String): Either<DomainError, AdminPanelUser>
  suspend fun getUserByEmail(email: String): Either<DomainError, AdminPanelUser>
  suspend fun insertUser(user: AdminPanelUser): Either<DomainError, Unit>
}

class AdminPanelUserRepositoryImpl(
  databaseProvider: DatabaseProvider,
) : AdminPanelUserRepository {

  private val database = databaseProvider.get()

  init {
    either {
      database.getRight().initTable(table = AdminPanelUserTable)
    }
  }

  override suspend fun getUserByUsername(username: String): Either<DomainError, AdminPanelUser> = either {
    database.getRight().dbQuery {
      AdminPanelUserDAO.find { AdminPanelUserTable.username eq username }.singleOrNull()?.toDomain()
        ?: raise(error = DomainError.Custom(e = NullPointerException("User not found")))
    }.getRight()
  }

  override suspend fun getUserByEmail(email: String): Either<DomainError, AdminPanelUser> = either {
    database.getRight().dbQuery {
      AdminPanelUserDAO.find { AdminPanelUserTable.email eq email }.singleOrNull()?.toDomain()
        ?: raise(error = DomainError.Custom(e = NullPointerException("User not found")))
    }.getRight()
  }

  override suspend fun insertUser(user: AdminPanelUser): Either<DomainError, Unit> = either {
    database.getRight().dbQuery {
      AdminPanelUserDAO.new {
        username = user.username
        email = user.email
        password = user.password
        salt = user.salt
      }
    }.getRight()
  }

}
