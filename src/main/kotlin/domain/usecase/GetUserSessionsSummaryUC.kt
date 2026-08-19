package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.data.repository.SessionParticipantsRepository
import pl.dev.bkwiatkowski.domain.model.UserSessionSummary

interface GetUserSessionsSummaryUC : UseCase<GetUserSessionsSummaryUC.Params, List<UserSessionSummary>> {
  data class Params(
    val userId: Int,
  ) : UseCase.Params
}

class GetUserSessionsSummaryUCImpl(
  private val sessionParticipantsRepository: SessionParticipantsRepository,
) : GetUserSessionsSummaryUC {
  override suspend fun invoke(params: GetUserSessionsSummaryUC.Params): Either<DomainError, List<UserSessionSummary>> =
    sessionParticipantsRepository.getUserSessionsSummary(userId = params.userId)
}
