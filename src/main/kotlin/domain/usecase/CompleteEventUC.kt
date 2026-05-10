package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.data.repository.EventRepository

interface CompleteEventUC : UseCase<CompleteEventUC.Params, Unit> {
  data class Params(
    val eventId: Int,
  ) : UseCase.Params
}

class CompleteEventUCImpl(
  private val eventRepository: EventRepository,
) : CompleteEventUC {
  override suspend fun invoke(params: CompleteEventUC.Params): Either<DomainError, Unit> {
    return eventRepository.completeEvent(id = params.eventId)
  }
}
