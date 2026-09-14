package pl.dev.bkwiatkowski.domain.model

data class TokenVerificationResult(
  val userId: Int,
  val isReused: Boolean
)
