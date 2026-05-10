package pl.dev.bkwiatkowski.data.repository

import org.jetbrains.exposed.v1.core.eq
import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.database.DatabaseProvider
import pl.dev.bkwiatkowski.core.database.dbQuery
import pl.dev.bkwiatkowski.core.database.initTable
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.data.dao.EventDAO
import pl.dev.bkwiatkowski.data.dao.EventWaypointDAO
import pl.dev.bkwiatkowski.data.entity.EventTable
import pl.dev.bkwiatkowski.data.entity.EventWaypointTable
import pl.dev.bkwiatkowski.data.mapper.toDomain
import pl.dev.bkwiatkowski.domain.model.Event
import pl.dev.bkwiatkowski.domain.model.EventStatus
import pl.dev.bkwiatkowski.data.dao.MapWaypointDAO
import pl.dev.bkwiatkowski.data.entity.MapWaypointTable

interface EventRepository {
  suspend fun getAllEventsByUserId(userId: Int): Either<DomainError, List<Event>>
  suspend fun getAllEvents(): Either<DomainError, List<Event>>
  suspend fun getEventById(id: Int): Either<DomainError, Event>
  suspend fun insertEvent(event: Event, waypointIds: List<Int>): Either<DomainError, Int>
  suspend fun deleteEvent(id: Int): Either<DomainError, Unit>
  suspend fun completeEvent(id: Int): Either<DomainError, Unit>
}

class EventRepositoryImpl(
  databaseProvider: DatabaseProvider,
) : EventRepository {

  private val database = databaseProvider.get()

  init {
    either {
      database.getRight().initTable(table = EventTable)
      database.getRight().initTable(table = EventWaypointTable)
    }
  }

  override suspend fun getAllEventsByUserId(userId: Int): Either<DomainError, List<Event>> = either {
    database.getRight().dbQuery {
      EventDAO.find { EventTable.userId eq userId }.map { event ->
        val waypointIds = EventWaypointDAO.find { EventWaypointTable.eventId eq event.id.value }.map { it.waypointId }.toList()
        val waypoints = MapWaypointDAO.find { MapWaypointTable.mapId eq event.mapId }
          .filter { it.id.value in waypointIds }
          .map { it.toDomain() }
        event.toDomain(mapWaypoints = waypoints)
      }
    }.getRight()
  }

  override suspend fun getAllEvents(): Either<DomainError, List<Event>> = either {
    database.getRight().dbQuery {
      EventDAO.all().map { event ->
        val waypointIds = EventWaypointDAO.find { EventWaypointTable.eventId eq event.id.value }.map { it.waypointId }.toList()
        val waypoints = MapWaypointDAO.find { MapWaypointTable.mapId eq event.mapId }
          .filter { it.id.value in waypointIds }
          .map { it.toDomain() }
        event.toDomain(mapWaypoints = waypoints)
      }
    }.getRight()
  }

  override suspend fun getEventById(id: Int): Either<DomainError, Event> = either {
    database.getRight().dbQuery {
      val eventDao = EventDAO.findById(id)
        ?: raise(error = DomainError.Custom(e = NullPointerException("Event not found")))

      val waypointIds = EventWaypointDAO.find { EventWaypointTable.eventId eq eventDao.id.value }.map { it.waypointId }.toList()
      val waypoints = MapWaypointDAO.find { MapWaypointTable.mapId eq eventDao.mapId }
        .filter { it.id.value in waypointIds }
        .map { it.toDomain() }
      eventDao.toDomain(mapWaypoints = waypoints)
    }.getRight()
  }

  override suspend fun insertEvent(event: Event, waypointIds: List<Int>): Either<DomainError, Int> = either {
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
        status = event.status.value
        allowOfflineTracking = event.allowOfflineTracking
        eventType = event.eventType.value
        finishedAt = event.finishedAt
      }

      waypointIds.forEach { waypointId ->
        EventWaypointDAO.new {
          eventId = newEvent.id.value
          this.waypointId = waypointId
        }
      }

      newEvent.id.value
    }.getRight()
  }

  override suspend fun deleteEvent(id: Int): Either<DomainError, Unit> = either {
    database.getRight().dbQuery {
      EventDAO.findById(id)?.delete()
        ?: raise(error = DomainError.Custom(e = NullPointerException("Event not found")))

      EventWaypointDAO.find { EventWaypointTable.eventId eq id }.forEach { it.delete() }
    }.getRight()
  }

  override suspend fun completeEvent(id: Int): Either<DomainError, Unit> = either {
    database.getRight().dbQuery {
      val event = EventDAO.findById(id)
        ?: raise(error = DomainError.Custom(e = NullPointerException("Event not found")))
      event.status = EventStatus.COMPLETED.value
      event.finishedAt = java.time.LocalDateTime.now()
    }.getRight()
  }
}