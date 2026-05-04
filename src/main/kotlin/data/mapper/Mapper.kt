package pl.dev.bkwiatkowski.data.mapper

import pl.dev.bkwiatkowski.data.dao.AdminPanelUserDAO
import pl.dev.bkwiatkowski.data.dao.EventDAO
import pl.dev.bkwiatkowski.data.dao.MapDAO
import pl.dev.bkwiatkowski.data.dao.MapWaypointDAO
import pl.dev.bkwiatkowski.domain.model.AdminPanelUser
import pl.dev.bkwiatkowski.domain.model.Event
import pl.dev.bkwiatkowski.domain.model.MapData
import pl.dev.bkwiatkowski.domain.model.MapWaypoint

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
  canPlayManually = canPlayManually,
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
)
