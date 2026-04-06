package pl.dev.bkwiatkowski.core

import io.ktor.server.config.ApplicationConfig
import io.ktor.util.reflect.TypeInfo
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

interface EnvironmentConfig {
  val jwtAudience: String
  val jwtRealm: String
  val jwtIssuer: String
  val jwtSecret: String
  val jwtExpiresIn: Duration
  val databaseHost: String
  val databasePort: String
  val databaseName: String
  val databaseUser: String
  val databasePassword: String
}

class EnvironmentConfigImpl(config: ApplicationConfig) : EnvironmentConfig {
  override val jwtAudience = config.property(path = "jwt.audience").getString()
  override val jwtRealm: String = config.property(path = "jwt.realm").getString()
  override val jwtIssuer: String = config.property(path = "jwt.domain").getString()
  override val jwtSecret: String = config.property(path = "jwt.secret").getString()
  override val jwtExpiresIn: Duration = config.property(path = "jwt.expiresInMinutes")
    .getString().toInt().toDuration(unit = DurationUnit.MINUTES)
  override val databaseHost: String = config.property(path = "postgres.host").getString()
  override val databasePort: String = config.property(path = "postgres.port").getString()
  override val databaseName: String = config.property(path = "postgres.database").getString()
  override val databaseUser: String = config.property(path = "postgres.user").getString()
  override val databasePassword: String = config.property(path = "postgres.password").getString()
}
