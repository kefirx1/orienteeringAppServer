package pl.dev.bkwiatkowski.core.security.token

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.EnvironmentConfig
import pl.dev.bkwiatkowski.core.either
import java.util.Date

class JwtTokenProvider(
  private val config: EnvironmentConfig,
) : TokenProvider {
  override fun generate(vararg claims: TokenClaim): Either<DomainError, String> = either {
    var token = JWT.create()
      .withAudience(config.jwtAudience)
      .withIssuer(config.jwtIssuer)
      .withExpiresAt(Date(System.currentTimeMillis() + config.jwtExpiresIn.inWholeMilliseconds))

    claims.forEach { claim ->
      token = token.withClaim(claim.name, claim.value)
    }

    token.sign(Algorithm.HMAC256(config.jwtSecret))
  }
}