package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.data.repository.MobileUserEventProgressionRepository
import pl.dev.bkwiatkowski.data.repository.MobileUserRepository
import pl.dev.bkwiatkowski.controller.admin.events.dto.response.EventParticipantProgressionDto
import java.time.temporal.ChronoUnit

interface GetEventParticipantsProgressionUC : UseCase<GetEventParticipantsProgressionUC.Params, List<EventParticipantProgressionDto>> {
  data class Params(
    val eventId: Int,
  ) : UseCase.Params
}

class GetEventParticipantsProgressionUCImpl(
  private val progressionRepository: MobileUserEventProgressionRepository,
  private val mobileUserRepository: MobileUserRepository,
) : GetEventParticipantsProgressionUC {
  override suspend fun invoke(params: GetEventParticipantsProgressionUC.Params): Either<DomainError, List<EventParticipantProgressionDto>> =
    either {
      val progressions = progressionRepository.getProgressionsByEventId(eventId = params.eventId)
        .getRight()
        .map { progression ->
          val user = mobileUserRepository.getUserById(id = progression.userId).getRight()
          EventParticipantProgressionDto(
            userId = progression.userId,
            userName = user.username,
            startedAt = progression.startedAt,
            finishedAt = progression.finishedAt,
            visitedWaypointsCount = progression.visitedWaypointsCount,
            isLiveTracking = progression.isLiveTracking,
          )
        }

      progressions.sortedWith(
        comparator = compareBy<EventParticipantProgressionDto> { -it.visitedWaypointsCount }
          .thenBy { progressionData ->
            if (progressionData.finishedAt != null) {
              ChronoUnit.SECONDS.between(progressionData.startedAt, progressionData.finishedAt)
            } else {
              Long.MAX_VALUE
            }
          }
      )
    }
}
