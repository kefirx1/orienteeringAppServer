package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.data.repository.EventRepository

interface DeleteEventUC : UseCase<DeleteEventUC.Params, Unit> {
  data class Params(
    val eventId: Int,
    val userId: Int,
    val isAdmin: Boolean,
  ) : UseCase.Params
}

class DeleteEventUCImpl(
  private val eventRepository: EventRepository,
) : DeleteEventUC {
  override suspend operator fun invoke(params: DeleteEventUC.Params): Either<DomainError, Unit> = either {
    val event = eventRepository.getEventById(id = params.eventId).getRight()

    if (!params.isAdmin && event.userId != params.userId) {
      raise(error = DomainError.Custom(e = Exception("Ownership assertion failed: user not authorized to delete this event")))
    }

    eventRepository.deleteEvent(id = params.eventId).getRight()
  }
}
