package pl.dev.bkwiatkowski.core.validation

class NotBlankRule(
  override val errorMessage: String = "Field cannot be blank"
) : ValidationRule {
  override fun validate(value: String): Boolean {
    return value.isNotBlank()
  }
}