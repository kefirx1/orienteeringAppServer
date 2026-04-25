package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.data.repository.MapRepository
import pl.dev.bkwiatkowski.domain.model.MapData

interface GetAllMapsUC : UseCase<GetAllMapsUC.Params, List<MapData>> {
  object Params : UseCase.Params
}

class GetAllMapsUCImpl(
  private val mapRepository: MapRepository,
) : GetAllMapsUC {
  override suspend operator fun invoke(params: GetAllMapsUC.Params): Either<DomainError, List<MapData>> = either {
    mapRepository.getAllMaps().getRight()
  }
}
