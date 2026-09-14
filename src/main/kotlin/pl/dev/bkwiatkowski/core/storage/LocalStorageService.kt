package pl.dev.bkwiatkowski.core.storage

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.EnvironmentConfig
import pl.dev.bkwiatkowski.core.either
import java.io.File
import java.nio.file.Files
import java.util.*

class LocalStorageService(config: EnvironmentConfig) : StorageService {
  private val storageDir = File(config.imagesStorageDir)

  init {
    if (!storageDir.exists()) {
      storageDir.mkdirs()
    }
  }

  override suspend fun store(
    bytes: ByteArray,
    extension: String,
    subfolder: String,
  ): Either<DomainError, String> = either {
    withContext(Dispatchers.IO) {
      val ext = if (extension.startsWith(".")) extension else ".$extension"
      val filename = "${UUID.randomUUID()}${ext}"

      val targetDir = if (subfolder.isNotBlank()) storageDir.resolve(subfolder) else storageDir
      if (!targetDir.exists()) {
        targetDir.mkdirs()
      }

      val target = targetDir.resolve(filename)
      Files.write(target.toPath(), bytes)

      if (subfolder.isNotBlank()) {
        "${subfolder.trimEnd('/')}/$filename"
      } else {
        filename
      }
    }
  }
}
