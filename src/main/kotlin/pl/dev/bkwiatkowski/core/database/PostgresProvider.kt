package pl.dev.bkwiatkowski.core.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.v1.jdbc.Database
import org.slf4j.LoggerFactory
import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.EnvironmentConfig
import pl.dev.bkwiatkowski.core.either

class PostgresProvider(private val config: EnvironmentConfig) : DatabaseProvider {

  val database: Database? = init()

  override fun get(): Either<DomainError, Database> = either {
    database ?: raise(
      error = DomainError.Custom(e = NullPointerException("Database not initialized")),
    )
  }

  private fun init(): Database? = either {
    val host = config.databaseHost
    val port = config.databasePort
    val databaseName = config.databaseName
    val user = config.databaseUser
    val password = config.databasePassword

    val jdbcUrl = "jdbc:postgresql://$host:$port/$databaseName"

    val hikariConfig = HikariConfig().apply {
      this.jdbcUrl = jdbcUrl
      this.driverClassName = "org.postgresql.Driver"
      this.username = user
      this.password = password
      this.maximumPoolSize = 10
      this.isAutoCommit = false
      this.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
      validate()
    }

    val dataSource = HikariDataSource(hikariConfig)
    LoggerFactory.getLogger("Database").info("Connecting to postgres database at $jdbcUrl")

    return Database.connect(dataSource)
  }.getRightOrNull()
}