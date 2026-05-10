package pl.dev.bkwiatkowski.data.repository

import org.jetbrains.exposed.v1.core.eq
import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.database.DatabaseProvider
import pl.dev.bkwiatkowski.core.database.dbQuery
import pl.dev.bkwiatkowski.core.database.initTable
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.data.dao.MapDAO
import pl.dev.bkwiatkowski.data.dao.MapWaypointDAO
import pl.dev.bkwiatkowski.data.entity.MapTable
import pl.dev.bkwiatkowski.data.entity.MapWaypointTable
import pl.dev.bkwiatkowski.data.mapper.toDomain
import pl.dev.bkwiatkowski.domain.model.MapData
import pl.dev.bkwiatkowski.domain.model.MapWaypoint

interface MapRepository {
  suspend fun getAllMaps(): Either<DomainError, List<MapData>>
  suspend fun getMapById(id: Int): Either<DomainError, MapData>
  suspend fun insertMap(map: MapData): Either<DomainError, Int>
  suspend fun deleteMap(id: Int): Either<DomainError, Unit>
  suspend fun addWaypoint(mapId: Int, waypoint: MapWaypoint): Either<DomainError, Unit>
}

class MapRepositoryImpl(
  databaseProvider: DatabaseProvider,
) : MapRepository {

  private val database = databaseProvider.get()

  init {
    either {
      database.getRight().initTable(table = MapTable)
      database.getRight().initTable(table = MapWaypointTable)
    }
  }

  override suspend fun getAllMaps(): Either<DomainError, List<MapData>> = either {
    database.getRight().dbQuery {
      MapDAO.all().map { mapDao ->
        val waypoints = MapWaypointDAO.find { MapWaypointTable.mapId eq mapDao.id.value }.map { it.toDomain() }
        mapDao.toDomain(mapWaypoints = waypoints)
      }
    }.getRight()
  }

  override suspend fun getMapById(id: Int): Either<DomainError, MapData> = either {
    database.getRight().dbQuery {
      val mapDao = MapDAO.findById(id)
        ?: raise(error = DomainError.Custom(e = NullPointerException("Map not found")))

      val waypoints = MapWaypointDAO.find { MapWaypointTable.mapId eq id }.map { it.toDomain() }

      mapDao.toDomain(mapWaypoints = waypoints)
    }.getRight()
  }

  override suspend fun insertMap(map: MapData): Either<DomainError, Int> = either {
    database.getRight().dbQuery {
      val newMap = MapDAO.new {
        name = map.name
        description = map.description
        imageData = map.imageData
      }

      map.mapWaypoints.forEach { waypoint ->
        MapWaypointDAO.new {
          mapId = newMap.id.value
          label = waypoint.label
          coordinateX = waypoint.coordinateX
          coordinateY = waypoint.coordinateY
        }
      }

      newMap.id.value
    }.getRight()
  }

  override suspend fun deleteMap(id: Int): Either<DomainError, Unit> = either {
    database.getRight().dbQuery {
      MapWaypointDAO.find { MapWaypointTable.mapId eq id }.forEach { it.delete() }
      MapDAO.findById(id)?.delete()
        ?: raise(error = DomainError.Custom(e = NullPointerException("Map not found")))
    }.getRight()
  }

  override suspend fun addWaypoint(mapId: Int, waypoint: MapWaypoint): Either<DomainError, Unit> = either {
    database.getRight().dbQuery {
      MapWaypointDAO.new {
        this.mapId = mapId
        label = waypoint.label
        coordinateX = waypoint.coordinateX
        coordinateY = waypoint.coordinateY
      }
    }.getRight()
  }
}
