package domain.repository

import java.time.LocalDateTime
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.domain.model.SessionParticipant
import pl.dev.bkwiatkowski.domain.model.SessionWaypointDetail
import pl.dev.bkwiatkowski.domain.model.UserSessionSummary

interface SessionParticipantsRepository {
  suspend fun addParticipantToSession(
    sessionUuid: String,
    userId: Int,
    joinedAt: LocalDateTime,
  ): Either<DomainError, SessionParticipant>

  suspend fun getSessionParticipants(sessionUuid: String): Either<DomainError, List<SessionParticipant>>

  suspend fun isUserInSession(sessionUuid: String, userId: Int): Either<DomainError, Boolean>

  suspend fun getSessionParticipant(sessionUuid: String, userId: Int): Either<DomainError, SessionParticipant>

  suspend fun recordWaypointVisit(
    sessionUuid: String,
    userId: Int,
    waypointId: Int,
    visitedAt: LocalDateTime,
    imagePath: String,
  ): Either<DomainError, SessionWaypointDetail>

  suspend fun getUserSessionWaypointDetails(
    sessionUuid: String,
    userId: Int,
    participantId: Int?,
  ): Either<DomainError, List<SessionWaypointDetail>>

  suspend fun getSessionWaypointDetails(sessionUuid: String): Either<DomainError, List<SessionWaypointDetail>>

  suspend fun getUserSessionsSummary(userId: Int): Either<DomainError, List<UserSessionSummary>>

  suspend fun finishParticipantSession(
    sessionUuid: String,
    userId: Int,
    finishedAt: LocalDateTime,
  ): Either<DomainError, SessionParticipant>
}