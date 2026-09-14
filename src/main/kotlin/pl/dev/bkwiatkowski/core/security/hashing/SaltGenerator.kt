package pl.dev.bkwiatkowski.core.security.hashing

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.either
import java.security.SecureRandom

interface SaltGenerator {
  fun generateSalt(saltLength: Int = 32): Either<DomainError, ByteArray>
}

class SaltGeneratorImpl : SaltGenerator {

  override fun generateSalt(saltLength: Int): Either<DomainError, ByteArray> = either {
    SecureRandom.getInstance(SALT_ALGORITHM).generateSeed(saltLength)
  }
}

private const val SALT_ALGORITHM = "SHA1PRNG"