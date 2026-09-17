package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.domain.repository.SessionParticipantsRepository
import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase

interface IsUserInSessionUC : UseCase<IsUserInSessionUC.Params, Boolean> {
  data class Params(
    val sessionUuid: String,
    val userId: Int,
  ) : UseCase.Params
}

class IsUserInSessionUCImpl(
  private val sessionParticipantsRepository: SessionParticipantsRepository,
) : IsUserInSessionUC {
  override suspend fun invoke(params: IsUserInSessionUC.Params): Either<DomainError, Boolean> =
    sessionParticipantsRepository.isUserInSession(
      sessionUuid = params.sessionUuid,
      userId = params.userId,
    )
}
