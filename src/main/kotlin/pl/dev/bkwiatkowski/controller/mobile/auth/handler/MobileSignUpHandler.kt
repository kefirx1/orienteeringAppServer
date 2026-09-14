package pl.dev.bkwiatkowski.controller.mobile.auth.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.controller.mobile.auth.dto.request.MobileSignUpRequestDto
import pl.dev.bkwiatkowski.controller.mobile.auth.dto.response.MobileSignInResponseDto
import pl.dev.bkwiatkowski.core.EnvironmentConfig
import pl.dev.bkwiatkowski.core.security.token.TokenClaim
import pl.dev.bkwiatkowski.core.security.token.TokenProvider
import pl.dev.bkwiatkowski.core.security.token.USER_ID_CLAIM
import pl.dev.bkwiatkowski.domain.usecase.GetMobileUserUC
import pl.dev.bkwiatkowski.domain.usecase.SaveMobileUserRefreshTokenUC
import java.time.Instant
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.core.validation.ValidationState
import pl.dev.bkwiatkowski.domain.usecase.AddNewMobileUserUC
import pl.dev.bkwiatkowski.domain.usecase.ValidateMobileUserRegistrationUC

class MobileSignUpHandler(
  private val addNewMobileUserUC: AddNewMobileUserUC,
  private val validateMobileUserRegistrationUC: ValidateMobileUserRegistrationUC,
  private val getMobileUserUC: GetMobileUserUC,
  private val tokenProvider: TokenProvider,
  private val environmentConfig: EnvironmentConfig,
  private val saveMobileUserRefreshTokenUC: SaveMobileUserRefreshTokenUC,
) {
  suspend fun handle(call: ApplicationCall) {
    val request = either {
      call.receiveNullable<MobileSignUpRequestDto>()
    }.getRightOrNull() ?: run {
      call.respond(
        status = HttpStatusCode.BadRequest,
        message = ErrorResponse(
          businessCode = "INVALID_REQUEST",
          message = "Invalid or missing request body",
        )
      )
      return
    }

    val validationResult = validateMobileUserRegistrationUC(
      params = ValidateMobileUserRegistrationUC.Params(
        username = request.username,
        email = request.email,
        password = request.password,
      )
    ).getRightOrElse {
      call.respond(
        status = HttpStatusCode.InternalServerError,
        message = ErrorResponse(
          businessCode = "INTERNAL_ERROR",
          message = "An unexpected error occurred during validation",
        )
      )
      return
    }

    if (validationResult is ValidationState.Invalid) {
      call.respond(
        status = HttpStatusCode.Conflict,
        message = ErrorResponse(
          businessCode = "VALIDATION_FAILED",
          message = validationResult.message,
        )
      )
      return
    }

    addNewMobileUserUC(
      params = AddNewMobileUserUC.Params(
        username = request.username,
        email = request.email,
        password = request.password,
        phoneNumber = request.phoneNumber,
        dateOfBirth = request.dateOfBirth,
      )
    ).getRightOrElse {
      call.respond(
        status = HttpStatusCode.Conflict,
        message = ErrorResponse(
          businessCode = "USER_CREATION_FAILED",
          message = "Failed to create user",
        )
      )
      return
    }

    val user = getMobileUserUC(
      params = GetMobileUserUC.Params(username = request.username),
    ).getRightOrElse {
      call.respond(
        status = HttpStatusCode.InternalServerError,
        message = ErrorResponse(
          businessCode = "USER_FETCH_ERROR",
          message = "Failed to fetch created user",
        )
      )
      return
    }

    val accessToken = tokenProvider.generate(
      TokenClaim(name = USER_ID_CLAIM, value = user.id.toString()),
    ).getRightOrElse {
      call.respond(
        status = HttpStatusCode.InternalServerError,
        message = ErrorResponse(
          businessCode = "TOKEN_GENERATION_ERROR",
          message = "Failed to generate authentication token",
        )
      )
      return
    }

    val refreshToken = tokenProvider.generateRefreshToken(
      TokenClaim(name = USER_ID_CLAIM, value = user.id.toString()),
    ).getRightOrElse {
      call.respond(
        status = HttpStatusCode.InternalServerError,
        message = ErrorResponse(
          businessCode = "TOKEN_GENERATION_ERROR",
          message = "Failed to generate refresh token",
        )
      )
      return
    }

    saveMobileUserRefreshTokenUC(
      params = SaveMobileUserRefreshTokenUC.Params(
        userId = user.id,
        token = refreshToken,
      )
    ).getRightOrElse {
      call.respond(
        status = HttpStatusCode.InternalServerError,
        message = ErrorResponse(
          businessCode = "DB_SAVE_ERROR",
          message = "Failed to save refresh token",
        )
      )
      return
    }

    val nowEpoch = Instant.now().epochSecond
    call.respond(
      status = HttpStatusCode.Created,
      message = MobileSignInResponseDto(
        accessToken = accessToken,
        refreshToken = refreshToken,
        accessTokenExpiresTimestamp = nowEpoch + environmentConfig.jwtExpiresIn.inWholeSeconds,
        refreshTokenExpiresTimestamp = nowEpoch + environmentConfig.jwtRefreshExpiresIn.inWholeSeconds,
      )
    )
  }
}
