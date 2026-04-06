package pl.dev.bkwiatkowski.core.database

import org.jetbrains.exposed.v1.jdbc.Database
import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either

interface DatabaseProvider {
  fun get(): Either<DomainError, Database>
}