package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.core.security.coder.ByteCoder
import pl.dev.bkwiatkowski.core.security.hashing.HashAlgorithm
import pl.dev.bkwiatkowski.core.security.hashing.HashGenerator
import pl.dev.bkwiatkowski.core.security.hashing.SaltGenerator
import pl.dev.bkwiatkowski.domain.model.SaltedHash

interface GenerateAdminPanelUserPasswordHashUC : UseCase<GenerateAdminPanelUserPasswordHashUC.Params, SaltedHash> {
  data class Params(
    val userPassword: String,
  ): UseCase.Params
}

class GenerateAdminPanelUserPasswordHashUCImpl(
  private val saltGenerator: SaltGenerator,
  private val hashGenerator: HashGenerator,
  private val byteCoder: ByteCoder,
): GenerateAdminPanelUserPasswordHashUC {
  override suspend fun invoke(params: GenerateAdminPanelUserPasswordHashUC.Params): Either<DomainError, SaltedHash> =
    either {
      val salt = saltGenerator.generateSalt().getRight()
      val hash = hashGenerator.hash(
        data = salt + params.userPassword.toByteArray(),
        algorithm = HashAlgorithm.SHA_3_256,
      ).getRight()

      SaltedHash(
        hash = byteCoder.encode(bytes = hash).getRight(),
        salt = byteCoder.encode(bytes = salt).getRight(),
      )
    }

}