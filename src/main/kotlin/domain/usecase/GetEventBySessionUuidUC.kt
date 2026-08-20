package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.data.repository.EventRepository
import pl.dev.bkwiatkowski.domain.model.Event

interface GetEventBySessionUuidUC : UseCase<GetEventBySessionUuidUC.Params, Event> {
  data class Params(val sessionUuid: String) : UseCase.Params
}

class GetEventBySessionUuidUCImpl(
  private val eventRepository: EventRepository,
) : GetEventBySessionUuidUC {
  override suspend fun invoke(params: GetEventBySessionUuidUC.Params): Either<DomainError, Event> = either {
    val session = eventRepository.getSessionByUuid(sessionUuid = params.sessionUuid).getRight()
      ?: raise(error = DomainError.Custom(IllegalStateException("Session not found")))

    eventRepository.getEventById(id = session.eventId).getRight()
  }
}
