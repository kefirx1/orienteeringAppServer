package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.core.security.coder.ByteCoder
import pl.dev.bkwiatkowski.core.security.hashing.HashAlgorithm
import pl.dev.bkwiatkowski.core.security.hashing.HashGenerator
import pl.dev.bkwiatkowski.core.security.hashing.SaltGenerator
import pl.dev.bkwiatkowski.data.repository.AdminPanelUserRepository
import pl.dev.bkwiatkowski.domain.model.SaltedHash

interface ChangePasswordUC : UseCase<ChangePasswordUC.Params, Unit> {
  data class Params(
    val userId: Int,
    val newPassword: String,
  ) : UseCase.Params
}

class ChangePasswordUCImpl(
  private val adminPanelUserRepository: AdminPanelUserRepository,
  private val saltGenerator: SaltGenerator,
  private val hashGenerator: HashGenerator,
  private val byteCoder: ByteCoder,
) : ChangePasswordUC {
  override suspend fun invoke(params: ChangePasswordUC.Params): Either<DomainError, Unit> =
    either {
      val salt = saltGenerator.generateSalt().getRight()
      val hash = hashGenerator.hash(
        data = salt + params.newPassword.toByteArray(),
        algorithm = HashAlgorithm.SHA_3_256,
      ).getRight()

      val saltedHash = SaltedHash(
        hash = byteCoder.encode(bytes = hash).getRight(),
        salt = byteCoder.encode(bytes = salt).getRight(),
      )

      adminPanelUserRepository.updateUserPassword(
        id = params.userId,
        saltedHash = saltedHash
      ).getRight()
    }
}
