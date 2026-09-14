package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.domain.repository.EventRepository
import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.core.either

interface GetLastEventUC : UseCase<GetLastEventUC.Params, Int> {
  object Params : UseCase.Params
}

class GetLastEventUCImpl(
  private val eventRepository: EventRepository,
) : GetLastEventUC {
  override suspend fun invoke(params: GetLastEventUC.Params): Either<DomainError, Int> = either {
    val events = eventRepository.getAllEvents().getRight()
    events.maxByOrNull { it.createdAt }?.id ?: raise(error = DomainError.Custom(NullPointerException("No events found")))
  }
}
