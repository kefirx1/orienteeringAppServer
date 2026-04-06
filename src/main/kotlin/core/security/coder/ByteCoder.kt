package pl.dev.bkwiatkowski.core.security.coder

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.either
import java.util.Base64

interface ByteCoder {
  fun encode(bytes: ByteArray): Either<DomainError, String>
  fun decode(encodedString: String): Either<DomainError, ByteArray>
}

class Base64ByteCoder : ByteCoder {
  override fun encode(bytes: ByteArray): Either<DomainError, String> = either {
    println("encode")
    Base64.getEncoder().encodeToString(bytes)
  }

  override fun decode(encodedString: String): Either<DomainError, ByteArray> = either {
    Base64.getDecoder().decode(encodedString)
  }
}
