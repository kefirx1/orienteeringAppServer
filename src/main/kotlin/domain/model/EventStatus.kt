package pl.dev.bkwiatkowski.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class EventStatus(val value: String) {
  PLANNED(value = "PLANNED"),
  IN_PROGRESS(value = "IN_PROGRESS"),
  COMPLETED(value = "COMPLETED"),
  CONTINUOUS(value = "CONTINUOUS");
}