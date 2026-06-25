package pl.dev.bkwiatkowski.controller.mobile.auth.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.controller.mobile.auth.dto.request.MobileSignInRequestDto
import pl.dev.bkwiatkowski.controller.mobile.auth.dto.response.MobileSignInResponseDto
import pl.dev.bkwiatkowski.core.EnvironmentConfig
import java.time.Instant
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.core.security.token.TokenClaim
import pl.dev.bkwiatkowski.core.security.token.TokenProvider
import pl.dev.bkwiatkowski.core.security.token.USER_ID_CLAIM
import pl.dev.bkwiatkowski.domain.model.SaltedHash
import pl.dev.bkwiatkowski.domain.usecase.GetMobileUserUC
import pl.dev.bkwiatkowski.domain.usecase.SaveMobileUserRefreshTokenUC
import pl.dev.bkwiatkowski.domain.usecase.VerifyMobileUserAuthenticationUC

class MobileSignInHandler(
  private val getMobileUserUC: GetMobileUserUC,
  private val verifyMobileUserAuthenticationUC: VerifyMobileUserAuthenticationUC,
  private val tokenProvider: TokenProvider,
  private val environmentConfig: EnvironmentConfig,
  private val saveMobileUserRefreshTokenUC: SaveMobileUserRefreshTokenUC,
) {
  suspend fun handle(call: ApplicationCall) {
    val request = either {
      call.receiveNullable<MobileSignInRequestDto>()
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

    val user = getMobileUserUC(
      params = GetMobileUserUC.Params(username = request.username),
    ).getRightOrElse {
      call.respond(
        status = HttpStatusCode.NotFound,
        message = ErrorResponse(
          businessCode = "USER_NOT_FOUND",
          message = "User does not exist",
        )
      )
      return
    }

    val result = verifyMobileUserAuthenticationUC(
      params = VerifyMobileUserAuthenticationUC.Params(
        password = request.password,
        saltedHash = SaltedHash(
          hash = user.password,
          salt = user.salt,
        ),
      ),
    ).getRightOrElse {
      call.respond(
        status = HttpStatusCode.InternalServerError,
        message = ErrorResponse(
          businessCode = "AUTHENTICATION_ERROR",
          message = "Failed to verify authentication",
        )
      )
      return
    }

    if (result == VerifyMobileUserAuthenticationUC.Result.InvalidCredentials) {
      call.respond(
        status = HttpStatusCode.Unauthorized,
        message = ErrorResponse(
          businessCode = "INVALID_CREDENTIALS",
          message = "Invalid username or password",
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
      status = HttpStatusCode.OK,
      message = MobileSignInResponseDto(
        accessToken = accessToken,
        refreshToken = refreshToken,
        accessTokenExpiresTimestamp = nowEpoch + environmentConfig.jwtExpiresIn.inWholeSeconds,
        refreshTokenExpiresTimestamp = nowEpoch + environmentConfig.jwtRefreshExpiresIn.inWholeSeconds,
      )
    )
  }
}
