package pl.dev.bkwiatkowski.domain.model

import java.time.LocalDate

data class MobileUser(
  val id: Int = 0,
  val username: String,
  val email: String,
  val password: String,
  val salt: String,
  val phoneNumber: String?,
  val dateOfBirth: LocalDate?,
)
