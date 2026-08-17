package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.controller.admin.events.dto.response.EventParticipantProgressionDto
import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.data.repository.MobileUserRepository
import pl.dev.bkwiatkowski.data.repository.SessionParticipantsRepository
import pl.dev.bkwiatkowski.data.repository.EventRepository
import java.time.temporal.ChronoUnit

interface GetEventParticipantsProgressionUC : UseCase<GetEventParticipantsProgressionUC.Params, List<EventParticipantProgressionDto>> {
  data class Params(
    val eventId: Int,
  ) : UseCase.Params
}

class GetEventParticipantsProgressionUCImpl(
  private val eventRepository: EventRepository,
  private val sessionParticipantsRepository: SessionParticipantsRepository,
  private val mobileUserRepository: MobileUserRepository,
) : GetEventParticipantsProgressionUC {
  override suspend fun invoke(params: GetEventParticipantsProgressionUC.Params): Either<DomainError, List<EventParticipantProgressionDto>> =
    either {
      val session = eventRepository.getSessionByEventId(eventId = params.eventId).getRight() ?: return@either emptyList()

      val participants = sessionParticipantsRepository.getSessionParticipants(sessionUuid = session.id).getRight()
      val sessionWaypointDetails = sessionParticipantsRepository.getSessionWaypointDetails(sessionUuid = session.id).getRight()

      val progressions = participants.map { participant ->
        val user = mobileUserRepository.getUserById(id = participant.userId).getRight()
        val visitedCount = sessionWaypointDetails.count { it.userId == participant.userId }

        EventParticipantProgressionDto(
          userId = participant.userId,
          userName = user.username,
          startedAt = participant.joinedAt,
          finishedAt = participant.finishedAt,
          visitedWaypointsCount = visitedCount,
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
