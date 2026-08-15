package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.data.repository.LocalFileRepository

interface StoreSessionImageUC : UseCase<StoreSessionImageUC.Params, String> {
  class Params(
    val imageBytes: ByteArray,
    val sessionUuid: String,
  ) : UseCase.Params
}

class StoreSessionImageUCImpl(
  private val localFileRepository: LocalFileRepository,
) : StoreSessionImageUC {

  companion object {
    private const val EXTENSION = "jpg"
  }

  override suspend operator fun invoke(params: StoreSessionImageUC.Params): Either<DomainError, String> =
    localFileRepository.store(bytes = params.imageBytes, extension = EXTENSION, subfolder = params.sessionUuid)
}
