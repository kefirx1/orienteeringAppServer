package pl.dev.bkwiatkowski.domain.model

data class AdminPanelUser(
  val id: Int = 0,
  val username: String,
  val email: String,
  val password: String,
  val salt: String,
  val role: Role,
) {
  enum class Role {
    USER, ADMIN
  }
}
