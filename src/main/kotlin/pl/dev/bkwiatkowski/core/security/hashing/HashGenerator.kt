package pl.dev.bkwiatkowski.core.security.hashing

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.either
import java.security.MessageDigest

enum class HashAlgorithm(val value: String) {
  SHA_256(value = "SHA-256"),
  SHA_3_256(value = "SHA3-256"),
}

interface HashGenerator {
  fun hash(
    data: ByteArray,
    algorithm: HashAlgorithm,
  ): Either<DomainError, ByteArray>
}

class HashGeneratorImpl : HashGenerator {

  override fun hash(
    data: ByteArray,
    algorithm: HashAlgorithm,
  ): Either<DomainError, ByteArray> = either {
    MessageDigest.getInstance(algorithm.value).digest(data)
  }

}