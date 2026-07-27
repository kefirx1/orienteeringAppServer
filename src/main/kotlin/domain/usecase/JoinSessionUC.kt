package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.data.repository.EventRepository
import pl.dev.bkwiatkowski.data.repository.SessionParticipantsRepository
import java.time.LocalDateTime

interface JoinSessionUC : UseCase<JoinSessionUC.Params, Unit> {
  data class Params(
    val sessionUuid: String,
    val userId: Int,
    val joinedAt: LocalDateTime = LocalDateTime.now(),
  ) : UseCase.Params
}

class JoinSessionUCImpl(
  private val eventRepository: EventRepository,
  private val sessionParticipantsRepository: SessionParticipantsRepository,
) : JoinSessionUC {
  override suspend fun invoke(params: JoinSessionUC.Params): Either<DomainError, Unit> = either {
    val session = eventRepository.getSessionByUuid(sessionUuid = params.sessionUuid)
      .getRight() ?: raise(DomainError.Custom(NullPointerException("Session not found")))

    if (session.finishedAt != null) {
      raise(DomainError.Custom(IllegalStateException("Session has already finished")))
    }

    if (!session.userCanJoin) {
      raise(DomainError.Custom(IllegalStateException("Session does not allow new participants to join")))
    }

    val isAlreadyInSession = sessionParticipantsRepository.isUserInSession(
      sessionUuid = params.sessionUuid,
      userId = params.userId,
    ).getRight()

    if (isAlreadyInSession) {
      raise(DomainError.Custom(IllegalStateException("User is already a participant in this session")))
    }

    sessionParticipantsRepository.addParticipantToSession(
      sessionUuid = params.sessionUuid,
      userId = params.userId,
      joinedAt = params.joinedAt,
    ).getRight()
  }
}
