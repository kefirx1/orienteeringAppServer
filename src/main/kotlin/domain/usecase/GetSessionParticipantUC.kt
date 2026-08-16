package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.data.repository.SessionParticipantsRepository
import pl.dev.bkwiatkowski.domain.model.SessionParticipant

interface GetSessionParticipantUC : UseCase<GetSessionParticipantUC.Params, SessionParticipant> {
  data class Params(
    val sessionUuid: String,
    val userId: Int,
  ) : UseCase.Params
}

class GetSessionParticipantUCImpl(
  private val sessionParticipantsRepository: SessionParticipantsRepository,
) : GetSessionParticipantUC {
  override suspend fun invoke(params: GetSessionParticipantUC.Params): Either<DomainError, SessionParticipant> =
    sessionParticipantsRepository.getSessionParticipant(
      sessionUuid = params.sessionUuid,
      userId = params.userId,
    )
}
