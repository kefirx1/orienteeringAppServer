package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.data.repository.EventRepository
import pl.dev.bkwiatkowski.domain.model.Event

interface GetEventByIdUC : UseCase<GetEventByIdUC.Params, Event> {
  data class Params(val eventId: Int) : UseCase.Params
}

class GetEventByIdUCImpl(
  private val eventRepository: EventRepository,
) : GetEventByIdUC {
  override suspend operator fun invoke(params: GetEventByIdUC.Params): Either<DomainError, Event> = either {
    eventRepository.getEventById(id = params.eventId).getRight()
  }
}
