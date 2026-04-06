package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.core.validation.EmailRule
import pl.dev.bkwiatkowski.core.validation.MinLengthRule
import pl.dev.bkwiatkowski.core.validation.NotBlankRule
import pl.dev.bkwiatkowski.core.validation.TextValidator
import pl.dev.bkwiatkowski.core.validation.ValidationState

interface ValidateAdminPanelUserRequestUC : UseCase<ValidateAdminPanelUserRequestUC.Params, ValidationState> {
    data class Params(
        val username: String,
        val email: String,
        val password: String,
    ) : UseCase.Params
}

class ValidateAdminPanelUserRequestUCImpl(
    private val usernameValidator: TextValidator,
    private val passwordValidator: TextValidator,
    private val emailValidator: TextValidator,
) : ValidateAdminPanelUserRequestUC {
    override suspend fun invoke(params: ValidateAdminPanelUserRequestUC.Params): Either<DomainError, ValidationState> = either {
        val userState = usernameValidator
            .addRule(rule = NotBlankRule(errorMessage = "Username cannot be blank"))
            .addRule(rule = MinLengthRule(minLength = 3, errorMessage = "Username must be at least 3 characters long"))
            .validate(value = params.username)
        if (userState is ValidationState.Invalid) {
            return@either userState
        }

        val passState = passwordValidator
            .addRule(rule = NotBlankRule(errorMessage = "Password cannot be blank"))
            .addRule(rule = MinLengthRule(minLength = 8, errorMessage = "Password must be at least 8 characters long"))
            .validate(value = params.password)
        if (passState is ValidationState.Invalid) {
            return@either passState
        }

        val mailState = emailValidator
            .addRule(rule = NotBlankRule(errorMessage = "Email cannot be blank"))
            .addRule(rule = EmailRule())
            .validate(value = params.email)
        if (mailState is ValidationState.Invalid) {
            return@either mailState
        }

        ValidationState.Valid
    }
}