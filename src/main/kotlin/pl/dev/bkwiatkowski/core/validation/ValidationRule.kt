package pl.dev.bkwiatkowski.core.validation

interface ValidationRule {
  val errorMessage: String
  fun validate(value: String): Boolean
}