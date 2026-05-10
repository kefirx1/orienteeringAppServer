package pl.dev.bkwiatkowski.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class EventType(val value: String) {
  ONLINE(value = "ONLINE"),
  OFFLINE(value = "OFFLINE");
}


