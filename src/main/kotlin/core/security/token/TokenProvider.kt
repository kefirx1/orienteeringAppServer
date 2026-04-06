package pl.dev.bkwiatkowski.core.security.token

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either

interface TokenProvider {
  fun generate(vararg claims: TokenClaim): Either<DomainError, String>
}

const val USER_ID_CLAIM = "userId"