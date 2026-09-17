package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.domain.model.SessionParticipant
import pl.dev.bkwiatkowski.domain.repository.SessionParticipantsRepository

interface AcceptParticipantUC : UseCase<AcceptParticipantUC.Params, Unit> {
  data class Params(
    val participantId: Int,
  ) : UseCase.Params
}

class AcceptParticipantUCImpl(
  private val sessionParticipantsRepository: SessionParticipantsRepository,
) : AcceptParticipantUC {
  override suspend fun invoke(params: AcceptParticipantUC.Params): Either<DomainError, Unit> =
    sessionParticipantsRepository.acceptParticipant(participantId = params.participantId)
}
