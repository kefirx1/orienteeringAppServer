package pl.dev.bkwiatkowski.core.security.token

data class TokenClaim(
  val name: String,
  val value: String,
)
