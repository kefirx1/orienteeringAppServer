package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.data.repository.MapRepository
import pl.dev.bkwiatkowski.domain.model.MapData
import pl.dev.bkwiatkowski.domain.model.MapWaypoint

interface AddMapUC : UseCase<AddMapUC.Params, Int> {
  data class Params(
    val name: String,
    val description: String,
    val imageData: String,
    val waypoints: List<MapWaypoint>,
  ) : UseCase.Params
}

class AddMapUCImpl(
  private val mapRepository: MapRepository,
) : AddMapUC {
  override suspend operator fun invoke(params: AddMapUC.Params): Either<DomainError, Int> = either {
    val newMap = MapData(
      name = params.name,
      description = params.description,
      imageData = params.imageData,
      mapWaypoints = params.waypoints,
    )

    mapRepository.insertMap(map = newMap).getRight()
  }
}
