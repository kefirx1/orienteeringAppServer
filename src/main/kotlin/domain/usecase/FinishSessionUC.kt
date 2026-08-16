package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.data.repository.SessionParticipantsRepository
import pl.dev.bkwiatkowski.domain.model.SessionParticipant
import java.time.LocalDateTime

interface FinishSessionUC : UseCase<FinishSessionUC.Params, SessionParticipant> {
  data class Params(
    val sessionUuid: String,
    val userId: Int,
    val finishedAt: LocalDateTime,
  ) : UseCase.Params
}

class FinishSessionUCImpl(
  private val sessionParticipantsRepository: SessionParticipantsRepository,
) : FinishSessionUC {
  override suspend fun invoke(params: FinishSessionUC.Params): Either<DomainError, SessionParticipant> =
    sessionParticipantsRepository.finishParticipantSession(
      sessionUuid = params.sessionUuid,
      userId = params.userId,
      finishedAt = params.finishedAt,
    )
}
