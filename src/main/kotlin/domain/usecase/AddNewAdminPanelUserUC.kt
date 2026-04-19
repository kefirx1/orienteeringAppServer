package pl.dev.bkwiatkowski.domain.usecase

import org.koin.core.logger.Logger
import pl.dev.bkwiatkowski.data.repository.AdminPanelUserRepository
import pl.dev.bkwiatkowski.domain.model.AdminPanelUser
import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.core.security.hashing.HashAlgorithm
import pl.dev.bkwiatkowski.core.security.hashing.HashGenerator
import pl.dev.bkwiatkowski.core.security.hashing.SaltGenerator
import pl.dev.bkwiatkowski.data.entity.AdminPanelUserTable.password

interface AddNewAdminPanelUserUC : UseCase<AddNewAdminPanelUserUC.Params, Unit> {
  data class Params(
    val username: String,
    val email: String,
    val password: String,
    val role: AdminPanelUser.Role = AdminPanelUser.Role.USER,
  ): UseCase.Params
}

class AddNewAdminPanelUserUCImpl(
  private val adminPanelUserRepository: AdminPanelUserRepository,
  private val generateAdminPanelUserPasswordHashUC: GenerateAdminPanelUserPasswordHashUC,
): AddNewAdminPanelUserUC {
  override suspend operator fun invoke(params: AddNewAdminPanelUserUC.Params): Either<DomainError, Unit> = either {
    val hashedPassword = generateAdminPanelUserPasswordHashUC(
      params = GenerateAdminPanelUserPasswordHashUC.Params(userPassword = params.password),
    ).getRight()

    val newUser = AdminPanelUser(
      username = params.username,
      email = params.email,
      password = hashedPassword.hash,
      salt = hashedPassword.salt,
      role = params.role,
    )

    adminPanelUserRepository.insertUser(user = newUser)
  }
}
