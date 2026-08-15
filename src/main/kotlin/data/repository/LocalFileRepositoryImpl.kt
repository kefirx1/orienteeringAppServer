package pl.dev.bkwiatkowski.data.repository

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.storage.StorageService

class LocalFileRepositoryImpl(
  private val storageService: StorageService,
) : LocalFileRepository {
  override suspend fun store(
    bytes: ByteArray,
    extension: String,
    subfolder: String,
  ): Either<DomainError, String> = storageService.store(bytes = bytes, extension = extension, subfolder = subfolder)
}
