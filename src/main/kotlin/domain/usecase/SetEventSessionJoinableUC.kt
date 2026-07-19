package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.data.repository.EventRepository

interface SetEventSessionJoinableUC : UseCase<SetEventSessionJoinableUC.Params, Unit> {
  data class Params(val eventId: Int, val userCanJoin: Boolean) : UseCase.Params
}

class SetEventSessionJoinableUCImpl(
  private val eventRepository: EventRepository,
) : SetEventSessionJoinableUC {
  override suspend fun invoke(params: SetEventSessionJoinableUC.Params): Either<DomainError, Unit> = either {
    eventRepository.setSessionUserCanJoin(eventId = params.eventId, userCanJoin = params.userCanJoin).getRight()
  }
}
