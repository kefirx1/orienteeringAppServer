package pl.dev.bkwiatkowski.data.dao

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import pl.dev.bkwiatkowski.data.entity.SessionParticipantsTable

class SessionParticipantDAO(id: EntityID<Int>) : IntEntity(id) {
  companion object : IntEntityClass<SessionParticipantDAO>(SessionParticipantsTable)

  var sessionUuid by SessionParticipantsTable.sessionUuid
  var userId by SessionParticipantsTable.userId
  var joinedAt by SessionParticipantsTable.joinedAt
  var finishedAt by SessionParticipantsTable.finishedAt
}
