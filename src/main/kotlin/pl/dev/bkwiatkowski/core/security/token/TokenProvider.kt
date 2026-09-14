package pl.dev.bkwiatkowski.core.security.token

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either

interface TokenProvider {
  fun generate(vararg claims: TokenClaim): Either<DomainError, String>
  fun generateRefreshToken(vararg claims: TokenClaim): Either<DomainError, String>
  fun verifyRefreshToken(token: String): Either<DomainError, String>
}

const val USER_ID_CLAIM = "userId"
const val USER_ROLE_CLAIM = "role"
const val TOKEN_TYPE_CLAIM = "type"

enum class TokenType(val value: String) {
  ACCESS(value = "ACCESS"),
  REFRESH(value = "REFRESH"),
}
