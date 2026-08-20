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
    val eventType: EventType,
  ) : UseCase.Params
}

class AddEventUCImpl(
  private val eventRepository: EventRepository,
  private val mapRepository: MapRepository,
  private val createEventSessionUC: CreateEventSessionUC,
) : AddEventUC {
  override suspend operator fun invoke(params: AddEventUC.Params): Either<DomainError, Int> = either {
    val map = mapRepository.getMapById(id = params.mapId).getRight()
    val now = LocalDateTime.now()

    val status = when (params.eventType) {
      EventType.OFFLINE -> EventStatus.CONTINUOUS
      EventType.ONLINE -> EventStatus.PLANNED
    }

    val startDate = when (params.eventType) {
      EventType.OFFLINE -> now
      EventType.ONLINE -> params.startDate
    }

    val newEvent = Event(
      map = map,
      userId = params.userId,
      name = params.name,
      description = params.description,
      createdAt = now,
      startDate = startDate,
      startLocationX = params.startLocationX,
      startLocationY = params.startLocationY,
      status = status,
      eventType = params.eventType,
    )

    val eventId = eventRepository.insertEvent(event = newEvent, waypointIds = params.waypointIds).getRight()

    if (params.eventType == EventType.OFFLINE) {
      createEventSessionUC(params = CreateEventSessionUC.Params(eventId = eventId)).getRight()
    }

    eventId
  }
}
