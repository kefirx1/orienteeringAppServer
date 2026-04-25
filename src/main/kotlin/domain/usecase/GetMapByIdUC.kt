package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.data.repository.MapRepository
import pl.dev.bkwiatkowski.domain.model.MapData

interface GetMapByIdUC : UseCase<GetMapByIdUC.Params, MapData> {
  data class Params(val mapId: Int) : UseCase.Params
}

class GetMapByIdUCImpl(
  private val mapRepository: MapRepository,
) : GetMapByIdUC {
  override suspend operator fun invoke(params: GetMapByIdUC.Params): Either<DomainError, MapData> = either {
    mapRepository.getMapById(id = params.mapId).getRight()
  }
}
