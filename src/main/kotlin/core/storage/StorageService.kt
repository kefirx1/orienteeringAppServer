package pl.dev.bkwiatkowski.core.storage

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either

interface StorageService {
  suspend fun store(
    bytes: ByteArray,
    extension: String,
    subfolder: String,
  ): Either<DomainError, String>
}
