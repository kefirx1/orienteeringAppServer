package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.domain.repository.EventRepository
import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.domain.model.EventSession

interface GetSessionByUuidUC : UseCase<GetSessionByUuidUC.Params, EventSession?> {
  data class Params(val sessionUuid: String) : UseCase.Params
}

class GetSessionByUuidUCImpl(
  private val eventRepository: EventRepository,
) : GetSessionByUuidUC {
  override suspend fun invoke(params: GetSessionByUuidUC.Params): Either<DomainError, EventSession?> =
    eventRepository.getSessionByUuid(sessionUuid = params.sessionUuid)
}
