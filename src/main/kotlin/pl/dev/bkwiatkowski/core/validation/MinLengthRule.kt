package pl.dev.bkwiatkowski.core.validation

class MinLengthRule(
  private val minLength: Int,
  override val errorMessage: String = "Field must be at least $minLength characters long"
) : ValidationRule {
  override fun validate(value: String): Boolean {
    return value.length >= minLength
  }
}