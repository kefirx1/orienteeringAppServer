package pl.dev.bkwiatkowski

import com.asyncapi.kotlinasyncapi.context.service.AsyncApiExtension
import com.asyncapi.kotlinasyncapi.ktor.AsyncApiPlugin
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.kborowy.authprovider.firebase.firebase
import io.github.flaxoos.ktor.server.plugins.ratelimiter.*
import io.github.flaxoos.ktor.server.plugins.ratelimiter.implementations.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.openapi.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.time.Duration
import kotlin.time.Duration.Companion.seconds
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureSecurity() {
  // Please read the jwt property from the config file if you are using EngineMain
  val jwtAudience = "jwt-audience"
  val jwtDomain = "https://jwt-provider-domain/"
  val jwtRealm = "ktor sample app"
  val jwtSecret = "secret"
  authentication {
    jwt {
      realm = jwtRealm
      verifier(
        JWT
          .require(Algorithm.HMAC256(jwtSecret))
          .withAudience(jwtAudience)
          .withIssuer(jwtDomain)
          .build()
      )
      validate { credential ->
        if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
      }
    }
  }
  install(Authentication) {
    firebase {
      adminFile = File("path/to/admin/file.json")
      realm = "My Server"
      validate { token ->
        MyAuthenticatedUser(id = token.uid)
      }
    }
  }
}
