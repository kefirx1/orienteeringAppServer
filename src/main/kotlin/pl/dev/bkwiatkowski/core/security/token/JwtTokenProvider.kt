package pl.dev.bkwiatkowski.core.security.token

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.EnvironmentConfig
import pl.dev.bkwiatkowski.core.either
import java.util.Date
import java.util.UUID
import kotlin.time.Clock.System.now

class JwtTokenProvider(
  private val config: EnvironmentConfig,
) : TokenProvider {
  override fun generate(vararg claims: TokenClaim): Either<DomainError, String> = either {
    var token = JWT.create()
      .withAudience(config.jwtAudience)
      .withIssuer(config.jwtIssuer)
      .withIssuedAt(Date())
      .withJWTId(UUID.randomUUID().toString())
      .withExpiresAt(Date(System.currentTimeMillis() + config.jwtExpiresIn.inWholeMilliseconds))

    claims.forEach { claim ->
      token = token.withClaim(claim.name, claim.value)
    }
    token.withClaim(
      TOKEN_TYPE_CLAIM, TokenType.ACCESS.value
    )

    token.sign(Algorithm.HMAC256(config.jwtSecret))
  }

  override fun generateRefreshToken(vararg claims: TokenClaim): Either<DomainError, String> = either {
    var token = JWT.create()
      .withAudience(config.jwtAudience)
      .withIssuer(config.jwtIssuer)
      .withIssuedAt(Date())
      .withJWTId(UUID.randomUUID().toString())
      .withExpiresAt(Date(System.currentTimeMillis() + config.jwtRefreshExpiresIn.inWholeMilliseconds))

    claims.forEach { claim ->
      token = token.withClaim(claim.name, claim.value)
    }
    token.withClaim(
      TOKEN_TYPE_CLAIM, TokenType.REFRESH.value
    )

    token.sign(Algorithm.HMAC256(config.jwtSecret))
  }

  override fun verifyRefreshToken(token: String): Either<DomainError, String> = either {
    val verifier = JWT.require(Algorithm.HMAC256(config.jwtSecret))
      .withAudience(config.jwtAudience)
      .withIssuer(config.jwtIssuer)
      .build()

    val decodedJWT = verifier.verify(token)
    decodedJWT.getClaim(USER_ID_CLAIM).asString()
  }
}
