package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.domain.repository.EventRepository
import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.core.either

interface CloseEventSessionUC : UseCase<CloseEventSessionUC.Params, Unit> {
  data class Params(val eventId: Int) : UseCase.Params
}

class CloseEventSessionUCImpl(
  private val eventRepository: EventRepository,
) : CloseEventSessionUC {
  override suspend fun invoke(params: CloseEventSessionUC.Params): Either<DomainError, Unit> = either {
    eventRepository.closeSessionForEvent(eventId = params.eventId, finishedAt = java.time.LocalDateTime.now()).getRight()
  }
}
