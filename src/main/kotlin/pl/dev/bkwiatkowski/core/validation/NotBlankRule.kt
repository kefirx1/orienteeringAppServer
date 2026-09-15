package pl.dev.bkwiatkowski.core.validation

class NotBlankRule(
  override val errorMessage: String = "Pole nie może być puste"
) : ValidationRule {
  override fun validate(value: String): Boolean {
    return value.isNotBlank()
  }
}