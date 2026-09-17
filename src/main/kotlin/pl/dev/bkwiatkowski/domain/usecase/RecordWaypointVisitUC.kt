package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.domain.repository.SessionParticipantsRepository
import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.domain.model.SessionWaypointDetail
import pl.dev.bkwiatkowski.domain.model.Accuracy
import java.time.LocalDateTime

interface RecordWaypointVisitUC : UseCase<RecordWaypointVisitUC.Params, SessionWaypointDetail> {
  data class Params(
    val sessionUuid: String,
    val userId: Int,
    val waypointId: Int,
    val visitedAt: LocalDateTime,
    val imagePath: String,
    val accuracy: Accuracy,
  ) : UseCase.Params
}

class RecordWaypointVisitUCImpl(
  private val sessionParticipantsRepository: SessionParticipantsRepository,
) : RecordWaypointVisitUC {
  override suspend fun invoke(params: RecordWaypointVisitUC.Params): Either<DomainError, SessionWaypointDetail> = either {
    sessionParticipantsRepository.recordWaypointVisit(
      sessionUuid = params.sessionUuid,
      userId = params.userId,
      waypointId = params.waypointId,
      visitedAt = params.visitedAt,
      imagePath = params.imagePath,
      accuracy = params.accuracy,
    ).getRight()
  }
}
