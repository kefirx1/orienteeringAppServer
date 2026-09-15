package pl.dev.bkwiatkowski.core.validation

class MinLengthRule(
  private val minLength: Int,
  override val errorMessage: String = "Wartość musi mieć co najmniej $minLength znaków"
) : ValidationRule {
  override fun validate(value: String): Boolean {
    return value.length >= minLength
  }
}