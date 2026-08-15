package pl.dev.bkwiatkowski.data.repository

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either

interface LocalFileRepository {
  suspend fun store(
    bytes: ByteArray,
    extension: String,
    subfolder: String,
  ): Either<DomainError, String>
}
