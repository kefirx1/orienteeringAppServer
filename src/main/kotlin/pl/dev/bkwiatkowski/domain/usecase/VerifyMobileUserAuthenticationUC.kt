package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.core.security.coder.ByteCoder
import pl.dev.bkwiatkowski.core.security.hashing.HashAlgorithm
import pl.dev.bkwiatkowski.core.security.hashing.HashGenerator
import pl.dev.bkwiatkowski.domain.model.SaltedHash

interface VerifyMobileUserAuthenticationUC :
  UseCase<VerifyMobileUserAuthenticationUC.Params, VerifyMobileUserAuthenticationUC.Result> {
  data class Params(
    val saltedHash: SaltedHash,
    val password: String,
  ) : UseCase.Params

  sealed interface Result {
    data object Success : Result
    data object InvalidCredentials : Result
  }
}

class VerifyMobileUserAuthenticationUCImpl(
  private val hashGenerator: HashGenerator,
  private val byteCoder: ByteCoder,
) : VerifyMobileUserAuthenticationUC {
  override suspend fun invoke(
    params: VerifyMobileUserAuthenticationUC.Params,
  ): Either<DomainError, VerifyMobileUserAuthenticationUC.Result> = either {
    val decodedSalt = byteCoder.decode(encodedString = params.saltedHash.salt).getRight()

    val typedPasswordHash = hashGenerator.hash(
      data = decodedSalt + params.password.toByteArray(),
      algorithm = HashAlgorithm.SHA_3_256,
    ).getRight()

    val encodedTypedHash = byteCoder.encode(bytes = typedPasswordHash).getRight()

    if (encodedTypedHash == params.saltedHash.hash) {
      VerifyMobileUserAuthenticationUC.Result.Success
    } else {
      VerifyMobileUserAuthenticationUC.Result.InvalidCredentials
    }
  }
}
