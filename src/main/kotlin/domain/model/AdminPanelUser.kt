package pl.dev.bkwiatkowski.domain.model

data class AdminPanelUser(
  val id: Int = 0,
  val username: String,
  val email: String,
  val password: String,
  val salt: String,
)
