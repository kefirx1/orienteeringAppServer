package pl.dev.bkwiatkowski.core.validation

interface TextValidator {
  fun addRule(rule: ValidationRule): TextValidator
  fun validate(value: String): ValidationState
}

class DefaultTextValidator : TextValidator {
  private val rules = mutableListOf<ValidationRule>()

  override fun addRule(rule: ValidationRule): TextValidator {
    rules.add(rule)
    return this
  }

  override fun validate(value: String): ValidationState {
    for (rule in rules) {
      if (!rule.validate(value)) {
        return ValidationState.Invalid(rule.errorMessage)
      }
    }
    return ValidationState.Valid
  }
}