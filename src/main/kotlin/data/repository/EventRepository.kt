package pl.dev.bkwiatkowski.data.repository

import org.jetbrains.exposed.v1.core.eq
import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.database.DatabaseProvider
import pl.dev.bkwiatkowski.core.database.dbQuery
import pl.dev.bkwiatkowski.core.database.initTable
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.data.dao.EventDAO
import pl.dev.bkwiatkowski.data.entity.EventTable
import pl.dev.bkwiatkowski.data.mapper.toDomain
import pl.dev.bkwiatkowski.domain.model.Event
import pl.dev.bkwiatkowski.data.dao.MapWaypointDAO
import pl.dev.bkwiatkowski.data.entity.MapWaypointTable

interface EventRepository {
  suspend fun getAllEventsByUserId(userId: Int): Either<DomainError, List<Event>>
  suspend fun getAllEvents(): Either<DomainError, List<Event>>
  suspend fun getEventById(id: Int): Either<DomainError, Event>
  suspend fun insertEvent(event: Event): Either<DomainError, Int>
  suspend fun deleteEvent(id: Int): Either<DomainError, Unit>
}

class EventRepositoryImpl(
  databaseProvider: DatabaseProvider,
) : EventRepository {

  private val database = databaseProvider.get()

  init {
    either {
      database.getRight().initTable(table = EventTable)
    }
  }

  override suspend fun getAllEventsByUserId(userId: Int): Either<DomainError, List<Event>> = either {
    database.getRight().dbQuery {
      EventDAO.find { EventTable.userId eq userId }.map { event ->
        val waypoints = MapWaypointDAO.find { MapWaypointTable.mapId eq event.mapId }.map { it.toDomain() }
        event.toDomain(mapWaypoints = waypoints)
      }
    }.getRight()
  }

  override suspend fun getAllEvents(): Either<DomainError, List<Event>> = either {
    database.getRight().dbQuery {
      EventDAO.all().map { event ->
        val waypoints = MapWaypointDAO.find { MapWaypointTable.mapId eq event.mapId }.map { it.toDomain() }
        event.toDomain(mapWaypoints = waypoints)
      }
    }.getRight()
  }

  override suspend fun getEventById(id: Int): Either<DomainError, Event> = either {
    database.getRight().dbQuery {
      val eventDao = EventDAO.findById(id)
        ?: raise(error = DomainError.Custom(e = NullPointerException("Event not found")))

      val waypoints = MapWaypointDAO.find { MapWaypointTable.mapId eq eventDao.mapId }.map { it.toDomain() }
      eventDao.toDomain(mapWaypoints = waypoints)
    }.getRight()
  }

  override suspend fun insertEvent(event: Event): Either<DomainError, Int> = either {
    database.getRight().dbQuery {
      val newEvent = EventDAO.new {
        mapId = event.map.id
        userId = event.userId
        name = event.name
        description = event.description
        createdAt = event.createdAt
        startDate = event.startDate
        startLocationX = event.startLocationX
        startLocationY = event.startLocationY
      }

      newEvent.id.value
    }.getRight()
  }

  override suspend fun deleteEvent(id: Int): Either<DomainError, Unit> = either {
    database.getRight().dbQuery {
      EventDAO.findById(id)?.delete()
        ?: raise(error = DomainError.Custom(e = NullPointerException("Event not found")))
    }.getRight()
  }
}
