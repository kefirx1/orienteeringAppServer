package pl.dev.bkwiatkowski.controller.mobile.events.dto

import kotlinx.serialization.Serializable

@Serializable
enum class AccuracyDto {
  STRONG,
  WEAK,
  VERY_WEAK,
}
