package pl.dev.bkwiatkowski.domain.repository

import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.domain.model.Event
import pl.dev.bkwiatkowski.domain.model.EventSession
import java.time.LocalDateTime

interface EventRepository {
  suspend fun getAllEventsByUserId(userId: Int): Either<DomainError, List<Event>>
  suspend fun getAllEvents(): Either<DomainError, List<Event>>
  suspend fun getEventById(id: Int): Either<DomainError, Event>
  suspend fun insertEvent(event: Event, waypointIds: List<Int>): Either<DomainError, Int>
  suspend fun deleteEvent(id: Int): Either<DomainError, Unit>
  suspend fun createSessionForEvent(eventId: Int): Either<DomainError, String>
  suspend fun getSessionByEventId(eventId: Int): Either<DomainError, EventSession?>
  suspend fun getSessionByUuid(sessionUuid: String): Either<DomainError, EventSession?>
  suspend fun setSessionUserCanJoin(eventId: Int, userCanJoin: Boolean): Either<DomainError, Unit>
  suspend fun closeSessionForEvent(eventId: Int, finishedAt: LocalDateTime): Either<DomainError, Unit>
}