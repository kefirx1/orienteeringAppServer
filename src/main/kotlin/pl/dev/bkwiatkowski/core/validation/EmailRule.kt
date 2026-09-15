package pl.dev.bkwiatkowski.core.validation

class EmailRule(
  override val errorMessage: String = "Zły format email"
) : ValidationRule {
  private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()

  override fun validate(value: String): Boolean {
    return value.matches(emailRegex)
  }
}