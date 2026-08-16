package pl.dev.bkwiatkowski.data.repository

import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.isNull
import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.database.DatabaseProvider
import pl.dev.bkwiatkowski.core.database.dbQuery
import pl.dev.bkwiatkowski.core.database.initTable
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.data.dao.SessionParticipantDAO
import pl.dev.bkwiatkowski.data.dao.SessionWaypointDetailDAO
import pl.dev.bkwiatkowski.data.entity.SessionParticipantsTable
import pl.dev.bkwiatkowski.data.entity.SessionWaypointDetailsTable
import pl.dev.bkwiatkowski.data.mapper.toDomain
import pl.dev.bkwiatkowski.domain.model.SessionParticipant
import pl.dev.bkwiatkowski.domain.model.SessionWaypointDetail
import java.time.LocalDateTime

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
  ): Either<DomainError, List<SessionWaypointDetail>>

  suspend fun getSessionWaypointDetails(sessionUuid: String): Either<DomainError, List<SessionWaypointDetail>>

  suspend fun finishParticipantSession(
    sessionUuid: String,
    userId: Int,
    finishedAt: LocalDateTime,
  ): Either<DomainError, SessionParticipant>
}

class SessionParticipantsRepositoryImpl(
  databaseProvider: DatabaseProvider,
) : SessionParticipantsRepository {

  private val database = databaseProvider.get()

  init {
    either {
      database.getRight().initTable(table = SessionParticipantsTable)
      database.getRight().initTable(table = SessionWaypointDetailsTable)
    }
  }

  override suspend fun addParticipantToSession(
    sessionUuid: String,
    userId: Int,
    joinedAt: LocalDateTime,
  ): Either<DomainError, SessionParticipant> = either {
    database.getRight().dbQuery {
      SessionParticipantDAO.new {
        this.sessionUuid = sessionUuid
        this.userId = userId
        this.joinedAt = joinedAt
      }.toDomain()
    }.getRight()
  }

  override suspend fun getSessionParticipants(sessionUuid: String): Either<DomainError, List<SessionParticipant>> = either {
    database.getRight().dbQuery {
      SessionParticipantDAO.find { SessionParticipantsTable.sessionUuid eq sessionUuid }
        .map { it.toDomain() }
        .toList()
    }.getRight()
  }

  override suspend fun isUserInSession(sessionUuid: String, userId: Int): Either<DomainError, Boolean> = either {
    database.getRight().dbQuery {
      SessionParticipantDAO.find {
        (SessionParticipantsTable.sessionUuid eq sessionUuid) and (SessionParticipantsTable.userId eq userId) and (SessionParticipantsTable.finishedAt.isNull())
      }.count() > 0
    }.getRight()
  }

  override suspend fun getSessionParticipant(sessionUuid: String, userId: Int): Either<DomainError, SessionParticipant> = either {
    database.getRight().dbQuery {
      SessionParticipantDAO.find {
        (SessionParticipantsTable.sessionUuid eq sessionUuid) and (SessionParticipantsTable.userId eq userId)
      }.firstOrNull()?.toDomain() ?: raise(error = DomainError.Custom(IllegalStateException("Participant not found")))
    }.getRight()
  }

  override suspend fun finishParticipantSession(
    sessionUuid: String,
    userId: Int,
    finishedAt: LocalDateTime,
  ): Either<DomainError, SessionParticipant> = either {
    database.getRight().dbQuery {
      val participant = SessionParticipantDAO.find {
        (SessionParticipantsTable.sessionUuid eq sessionUuid) and (SessionParticipantsTable.userId eq userId) and (SessionParticipantsTable.finishedAt.isNull())
      }.firstOrNull() ?: raise(error = DomainError.Custom(IllegalStateException("Participant not found or already finished")))

      participant.finishedAt = finishedAt
      participant.toDomain()
    }.getRight()
  }
  override suspend fun recordWaypointVisit(
    sessionUuid: String,
    userId: Int,
    waypointId: Int,
    visitedAt: LocalDateTime,
    imagePath: String,
  ): Either<DomainError, SessionWaypointDetail> = either {
    database.getRight().dbQuery {
      SessionWaypointDetailDAO.new {
        this.sessionUuid = sessionUuid
        this.userId = userId
        this.waypointId = waypointId
        this.visitedAt = visitedAt
        this.imagePath = imagePath
      }.toDomain()
    }.getRight()
  }

  override suspend fun getUserSessionWaypointDetails(
    sessionUuid: String,
    userId: Int,
  ): Either<DomainError, List<SessionWaypointDetail>> = either {
    database.getRight().dbQuery {
      SessionWaypointDetailDAO.find {
        (SessionWaypointDetailsTable.sessionUuid eq sessionUuid) and (SessionWaypointDetailsTable.userId eq userId)
      }.map { it.toDomain() }
        .toList()
    }.getRight()
  }

  override suspend fun getSessionWaypointDetails(sessionUuid: String): Either<DomainError, List<SessionWaypointDetail>> = either {
    database.getRight().dbQuery {
      SessionWaypointDetailDAO.find { SessionWaypointDetailsTable.sessionUuid eq sessionUuid }
        .map { it.toDomain() }
        .toList()
    }.getRight()
  }
}
