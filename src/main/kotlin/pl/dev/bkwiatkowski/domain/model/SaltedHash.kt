package pl.dev.bkwiatkowski.domain.model

data class SaltedHash(
  val hash: String,
  val salt: String,
)
