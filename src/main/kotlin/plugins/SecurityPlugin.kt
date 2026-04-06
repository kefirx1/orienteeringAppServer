package pl.dev.bkwiatkowski.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import pl.dev.bkwiatkowski.core.EnvironmentConfig

class SecurityPlugin(
  private val environmentConfig: EnvironmentConfig
) {
  fun configure(application: Application) {
    application.authentication {
      jwt {
        realm = environmentConfig.jwtRealm

        verifier(
          JWT.require(Algorithm.HMAC256(environmentConfig.jwtSecret))
            .withAudience(environmentConfig.jwtAudience)
            .withIssuer(environmentConfig.jwtIssuer)
            .build()
        )
        validate { credentials ->
          if (credentials.payload.audience.contains(environmentConfig.jwtAudience)) {
            JWTPrincipal(credentials.payload)
          } else {
            null
          }
        }
      }
    }
  }
}
