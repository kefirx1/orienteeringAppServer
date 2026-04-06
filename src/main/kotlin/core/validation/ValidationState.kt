package pl.dev.bkwiatkowski.core.validation

sealed interface ValidationState {
  data object Valid : ValidationState
  data class Invalid(val errorMessage: String) : ValidationState
}