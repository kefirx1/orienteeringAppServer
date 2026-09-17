package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.domain.repository.EventRepository
import domain.repository.MobileUserRepository
import pl.dev.bkwiatkowski.domain.repository.SessionParticipantsRepository
import pl.dev.bkwiatkowski.controller.admin.events.dto.response.EventParticipantProgressionDto
import pl.dev.bkwiatkowski.domain.model.Accuracy
import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.core.either

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
        val visitedCount = sessionWaypointDetails.count { it.participantId == participant.id }
        val hasLowAccuracy = sessionWaypointDetails.any { it.participantId == participant.id && (it.accuracy == Accuracy.WEAK || it.accuracy == Accuracy.VERY_WEAK) }

        EventParticipantProgressionDto(
          participantId = participant.id,
          userId = participant.userId,
          userName = user.username,
          startedAt = participant.joinedAt,
          finishedAt = participant.finishedAt,
          visitedWaypointsCount = visitedCount,
          hasLowAccuracy = hasLowAccuracy,
          hasToBeChecked = participant.hasToBeChecked,
        )
      }

      progressions.sortedByDescending { it.participantId }
    }
}
