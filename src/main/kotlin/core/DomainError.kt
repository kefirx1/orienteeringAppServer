package pl.dev.bkwiatkowski.core

sealed interface DomainError {
  data class Custom(
    val e: Throwable? = null,
  ): DomainError
}