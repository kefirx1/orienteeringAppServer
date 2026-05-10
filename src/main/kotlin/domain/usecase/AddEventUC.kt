package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.data.repository.EventRepository
import pl.dev.bkwiatkowski.data.repository.MapRepository
import pl.dev.bkwiatkowski.domain.model.Event
import pl.dev.bkwiatkowski.domain.model.EventType
import pl.dev.bkwiatkowski.domain.model.EventStatus
import java.time.LocalDateTime

interface AddEventUC : UseCase<AddEventUC.Params, Int> {
  data class Params(
    val mapId: Int,
    val userId: Int,
    val name: String,
    val description: String,
    val startDate: LocalDateTime,
    val startLocationX: Float,
    val startLocationY: Float,
    val waypointIds: List<Int>,
    val allowOfflineTracking: Boolean = false,
    val eventType: EventType,
  ) : UseCase.Params
}

class AddEventUCImpl(
  private val eventRepository: EventRepository,
  private val mapRepository: MapRepository,
) : AddEventUC {
  override suspend operator fun invoke(params: AddEventUC.Params): Either<DomainError, Int> = either {
    val map = mapRepository.getMapById(id = params.mapId).getRight()
    val now = LocalDateTime.now()

    val (status, finishedAt) = when (params.eventType) {
      EventType.OFFLINE -> EventStatus.CONTINUOUS to now
      EventType.ONLINE -> EventStatus.PLANNED to null
    }

    val newEvent = Event(
      map = map,
      userId = params.userId,
      name = params.name,
      description = params.description,
      createdAt = now,
      startDate = params.startDate,
      startLocationX = params.startLocationX,
      startLocationY = params.startLocationY,
      allowOfflineTracking = params.allowOfflineTracking,
      status = status,
      finishedAt = finishedAt,
      eventType = params.eventType,
    )

    eventRepository.insertEvent(event = newEvent, waypointIds = params.waypointIds).getRight()
  }
}
