package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.data.repository.EventRepository
import pl.dev.bkwiatkowski.data.repository.MapRepository
import pl.dev.bkwiatkowski.domain.model.Event
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
  ) : UseCase.Params
}

class AddEventUCImpl(
  private val eventRepository: EventRepository,
  private val mapRepository: MapRepository,
) : AddEventUC {
  override suspend operator fun invoke(params: AddEventUC.Params): Either<DomainError, Int> = either {
    val map = mapRepository.getMapById(id = params.mapId).getRight()

    val newEvent = Event(
      map = map,
      userId = params.userId,
      name = params.name,
      description = params.description,
      createdAt = LocalDateTime.now(),
      startDate = params.startDate,
      startLocationX = params.startLocationX,
      startLocationY = params.startLocationY,
    )

    eventRepository.insertEvent(event = newEvent).getRight()
  }
}
