package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.data.repository.EventRepository
import pl.dev.bkwiatkowski.domain.model.Event

interface GetAllEventsUC : UseCase<GetAllEventsUC.Params, List<Event>> {
  data class Params(
    val userId: Int,
    val isAdmin: Boolean,
  ) : UseCase.Params
}

class GetAllEventsUCImpl(
  private val eventRepository: EventRepository,
) : GetAllEventsUC {
  override suspend operator fun invoke(params: GetAllEventsUC.Params): Either<DomainError, List<Event>> = either {
    if (params.isAdmin) {
      eventRepository.getAllEvents().getRight()
    } else {
      eventRepository.getAllEventsByUserId(userId = params.userId).getRight()
    }
  }
}
