package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.domain.repository.EventRepository
import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.core.either

interface CreateEventSessionUC : UseCase<CreateEventSessionUC.Params, String> {
  data class Params(val eventId: Int) : UseCase.Params
}

class CreateEventSessionUCImpl(
  private val eventRepository: EventRepository,
) : CreateEventSessionUC {
  override suspend fun invoke(params: CreateEventSessionUC.Params): Either<DomainError, String> = either {
    eventRepository.createSessionForEvent(eventId = params.eventId).getRight()
  }
}
