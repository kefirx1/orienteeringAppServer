package pl.dev.bkwiatkowski.core.response

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
  val businessCode: String,
  val message: String,
  val showMessage: Boolean = false,
)
