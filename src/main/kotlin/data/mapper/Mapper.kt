package pl.dev.bkwiatkowski.data.mapper

import pl.dev.bkwiatkowski.data.dao.*
import pl.dev.bkwiatkowski.domain.model.*

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

fun EventDAO.toDomain(mapWaypoints: List<MapWaypoint>, eventWaypoints: List<MapWaypoint>) = Event(
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
  eventWaypoints = eventWaypoints,
  allowOfflineTracking = allowOfflineTracking,
  eventType = when (eventType) {
    "OFFLINE" -> EventType.OFFLINE
    else -> EventType.ONLINE
  },
)

fun SessionParticipantDAO.toDomain() = SessionParticipant(
  id = id.value,
  sessionUuid = sessionUuid,
  userId = userId,
  joinedAt = joinedAt,
  finishedAt = finishedAt,
)

fun SessionWaypointDetailDAO.toDomain() = SessionWaypointDetail(
  id = id.value,
  sessionUuid = sessionUuid,
  userId = userId,
  waypointId = waypointId,
  visitedAt = visitedAt,
  imagePath = imagePath,
)