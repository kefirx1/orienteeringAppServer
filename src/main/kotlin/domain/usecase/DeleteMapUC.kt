package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.data.repository.MapRepository

interface DeleteMapUC : UseCase<DeleteMapUC.Params, Unit> {
  data class Params(val mapId: Int) : UseCase.Params
}

class DeleteMapUCImpl(
  private val mapRepository: MapRepository,
) : DeleteMapUC {
  override suspend operator fun invoke(params: DeleteMapUC.Params): Either<DomainError, Unit> = either {
    mapRepository.deleteMap(id = params.mapId).getRight()
  }
}
