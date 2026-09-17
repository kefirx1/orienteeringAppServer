package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.domain.model.SessionParticipant
import pl.dev.bkwiatkowski.domain.repository.SessionParticipantsRepository

interface GetSessionParticipantByIdUC : UseCase<GetSessionParticipantByIdUC.Params, SessionParticipant> {
  data class Params(
    val participantId: Int,
  ) : UseCase.Params
}

class GetSessionParticipantByIdUCImpl(
  private val sessionParticipantsRepository: SessionParticipantsRepository,
) : GetSessionParticipantByIdUC {
  override suspend fun invoke(params: GetSessionParticipantByIdUC.Params): Either<DomainError, SessionParticipant> =
    sessionParticipantsRepository.getSessionParticipantById(participantId = params.participantId)
}
