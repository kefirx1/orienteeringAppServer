package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.data.repository.SessionParticipantsRepository
import pl.dev.bkwiatkowski.domain.model.SessionParticipant

interface GetFinishedSessionParticipantsUC : UseCase<GetFinishedSessionParticipantsUC.Params, List<SessionParticipant>> {
  data class Params(val sessionUuid: String, val userId: Int) : UseCase.Params
}

class GetFinishedSessionParticipantsUCImpl(
  private val sessionParticipantsRepository: SessionParticipantsRepository,
) : GetFinishedSessionParticipantsUC {
  override suspend fun invoke(params: GetFinishedSessionParticipantsUC.Params): Either<DomainError, List<SessionParticipant>> = either {
    sessionParticipantsRepository.getSessionParticipants(sessionUuid = params.sessionUuid).getRight()
      .filter { it.finishedAt != null && it.userId == params.userId }
  }
}
