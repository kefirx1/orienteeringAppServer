package pl.dev.bkwiatkowski.data.repository

import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.database.DatabaseProvider
import pl.dev.bkwiatkowski.core.database.dbQuery
import pl.dev.bkwiatkowski.core.database.initTable
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.data.dao.MobileUserEventProgressionDAO
import pl.dev.bkwiatkowski.data.entity.MobileUserEventProgressionTable
import pl.dev.bkwiatkowski.data.mapper.toDomain
import pl.dev.bkwiatkowski.domain.model.MobileUserEventProgression

interface MobileUserEventProgressionRepository {
  suspend fun saveProgression(progression: MobileUserEventProgression): Either<DomainError, MobileUserEventProgression>
  suspend fun finishProgression(
    progressionId: Int,
    visitedWaypointsCount: Int,
  ): Either<DomainError, Unit>
  suspend fun getProgressionById(id: Int): Either<DomainError, MobileUserEventProgression>
  suspend fun getProgressionsByUserId(userId: Int): Either<DomainError, List<MobileUserEventProgression>>
  suspend fun getProgressionsByEventId(eventId: Int): Either<DomainError, List<MobileUserEventProgression>>
  suspend fun getOngoingProgressionForUser(userId: Int): Either<DomainError, MobileUserEventProgression?>
}

class MobileUserEventProgressionRepositoryImpl(
  databaseProvider: DatabaseProvider,
) : MobileUserEventProgressionRepository {

  private val database = databaseProvider.get()

  init {
    either {
      database.getRight().initTable(table = MobileUserEventProgressionTable)
    }
  }

  override suspend fun saveProgression(progression: MobileUserEventProgression): Either<DomainError, MobileUserEventProgression> =
    either {
      database.getRight().dbQuery {
        MobileUserEventProgressionDAO.new {
          userId = progression.userId
          eventId = progression.eventId
          startedAt = progression.startedAt
          finishedAt = progression.finishedAt
          visitedWaypointsCount = progression.visitedWaypointsCount
          isLiveTracking = progression.isLiveTracking
        }.toDomain()
      }.getRight()
    }

  override suspend fun finishProgression(
    progressionId: Int,
    visitedWaypointsCount: Int,
  ): Either<DomainError, Unit> = either {
    database.getRight().dbQuery {
      val progression = MobileUserEventProgressionDAO.findById(progressionId)
        ?: raise(error = DomainError.Custom(e = NullPointerException("Progression not found")))
      progression.finishedAt = java.time.LocalDateTime.now()
      progression.visitedWaypointsCount = visitedWaypointsCount
    }.getRight()
  }

  override suspend fun getProgressionById(id: Int): Either<DomainError, MobileUserEventProgression> = either {
    database.getRight().dbQuery {
      MobileUserEventProgressionDAO.findById(id)?.toDomain()
        ?: raise(error = DomainError.Custom(e = NullPointerException("Progression not found")))
    }.getRight()
  }

  override suspend fun getProgressionsByUserId(userId: Int): Either<DomainError, List<MobileUserEventProgression>> =
    either {
      database.getRight().dbQuery {
        MobileUserEventProgressionDAO.find { MobileUserEventProgressionTable.userId eq userId }
          .map { it.toDomain() }
      }.getRight()
    }

  override suspend fun getProgressionsByEventId(eventId: Int): Either<DomainError, List<MobileUserEventProgression>> =
    either {
      database.getRight().dbQuery {
        MobileUserEventProgressionDAO.find { MobileUserEventProgressionTable.eventId eq eventId }
          .map { it.toDomain() }
      }.getRight()
    }

  override suspend fun getOngoingProgressionForUser(userId: Int): Either<DomainError, MobileUserEventProgression?> =
    either {
      database.getRight().dbQuery {
        MobileUserEventProgressionDAO.find {
          (MobileUserEventProgressionTable.userId eq userId) and
              (MobileUserEventProgressionTable.finishedAt eq null)
        }.singleOrNull()?.toDomain()
      }.getRight()
    }
}
