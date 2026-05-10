package pl.dev.bkwiatkowski.data.mapper

import pl.dev.bkwiatkowski.data.dao.AdminPanelUserDAO
import pl.dev.bkwiatkowski.data.dao.EventDAO
import pl.dev.bkwiatkowski.data.dao.MapDAO
import pl.dev.bkwiatkowski.data.dao.MapWaypointDAO
import pl.dev.bkwiatkowski.data.dao.MobileUserDAO
import pl.dev.bkwiatkowski.data.dao.MobileUserEventProgressionDAO
import pl.dev.bkwiatkowski.domain.model.AdminPanelUser
import pl.dev.bkwiatkowski.domain.model.Event
import pl.dev.bkwiatkowski.domain.model.EventStatus
import pl.dev.bkwiatkowski.domain.model.EventType
import pl.dev.bkwiatkowski.domain.model.MapData
import pl.dev.bkwiatkowski.domain.model.MapWaypoint
import pl.dev.bkwiatkowski.domain.model.MobileUser
import pl.dev.bkwiatkowski.domain.model.MobileUserEventProgression

fun MobileUserDAO.toDomain() = MobileUser(
  id = id.value,
  username = username,
  email = email,
  password = password,
  salt = salt,
  phoneNumber = phoneNumber,
  dateOfBirth = dateOfBirth,
)

fun MobileUserEventProgressionDAO.toDomain() = MobileUserEventProgression(
  id = id.value,
  userId = userId,
  eventId = eventId,
  startedAt = startedAt,
  finishedAt = finishedAt,
  visitedWaypointsCount = visitedWaypointsCount,
  isLiveTracking = isLiveTracking,
)

fun AdminPanelUserDAO.toDomain() = AdminPanelUser(
  id = id.value,
  username = username,
  email = email,
  password = password,
  salt = salt,
  role = role
)

fun MapWaypointDAO.toDomain() = MapWaypoint(
  id = id.value,
  label = label,
  coordinateX = coordinateX,
  coordinateY = coordinateY,
)

fun MapDAO.toDomain(mapWaypoints: List<MapWaypoint>) = MapData(
  id = id.value,
  name = name,
  description = description,
  imageData = imageData,
  mapWaypoints = mapWaypoints,
)

fun EventDAO.toDomain(mapWaypoints: List<MapWaypoint>) = Event(
  id = id.value,
  map = map.toDomain(mapWaypoints = mapWaypoints),
  userId = userId,
  name = name,
  description = description,
  createdAt = createdAt,
  startDate = startDate,
  startLocationX = startLocationX,
  startLocationY = startLocationY,
  status = when (status) {
    "IN_PROGRESS" -> EventStatus.IN_PROGRESS
    "COMPLETED" -> EventStatus.COMPLETED
    "CONTINUOUS" -> EventStatus.CONTINUOUS
    else -> EventStatus.PLANNED
  },
  finishedAt = finishedAt,
  allowOfflineTracking = allowOfflineTracking,
  eventType = when (eventType) {
    "OFFLINE" -> EventType.OFFLINE
    else -> EventType.ONLINE
  },
)
