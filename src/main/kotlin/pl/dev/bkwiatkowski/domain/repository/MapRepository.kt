package domain.repository

import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.domain.model.MapData
import pl.dev.bkwiatkowski.domain.model.MapWaypoint

interface MapRepository {
  suspend fun getAllMaps(): Either<DomainError, List<MapData>>
  suspend fun getMapById(id: Int): Either<DomainError, MapData>
  suspend fun insertMap(map: MapData): Either<DomainError, Int>
  suspend fun deleteMap(id: Int): Either<DomainError, Unit>
  suspend fun addWaypoint(mapId: Int, waypoint: MapWaypoint): Either<DomainError, Unit>
}