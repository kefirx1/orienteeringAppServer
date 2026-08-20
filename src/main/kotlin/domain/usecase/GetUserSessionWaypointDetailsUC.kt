package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.data.repository.SessionParticipantsRepository
import pl.dev.bkwiatkowski.domain.model.SessionWaypointDetail

interface GetUserSessionWaypointDetailsUC : UseCase<GetUserSessionWaypointDetailsUC.Params, List<SessionWaypointDetail>> {
  data class Params(
    val sessionUuid: String,
    val userId: Int,
    val participantId: Int?,
  ) : UseCase.Params
}

class GetUserSessionWaypointDetailsUCImpl(
  private val sessionParticipantsRepository: SessionParticipantsRepository,
) : GetUserSessionWaypointDetailsUC {
  override suspend fun invoke(params: GetUserSessionWaypointDetailsUC.Params): Either<DomainError, List<SessionWaypointDetail>> =
    sessionParticipantsRepository.getUserSessionWaypointDetails(
      sessionUuid = params.sessionUuid,
      userId = params.userId,
      participantId = params.participantId,
    )
}
