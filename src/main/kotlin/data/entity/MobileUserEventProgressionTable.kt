package pl.dev.bkwiatkowski.data.entity

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.javatime.datetime

object MobileUserEventProgressionTable : IntIdTable(name = "mobile_user_event_progressions") {
  val userId = integer(name = "user_id").references(MobileUserTable.id)
  val eventId = integer(name = "event_id").references(EventTable.id)
  val startedAt = datetime(name = "started_at")
  val finishedAt = datetime(name = "finished_at").nullable()
  val visitedWaypointsCount = integer(name = "visited_waypoints_count").default(0)
  val isLiveTracking = bool(name = "is_live_tracking").default(true)
}
