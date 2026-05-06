package pl.dev.bkwiatkowski.controller.mobile.auth.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.controller.mobile.auth.dto.request.MobileRefreshTokenRequestDto
import pl.dev.bkwiatkowski.controller.mobile.auth.dto.response.MobileSignInResponseDto
import pl.dev.bkwiatkowski.core.EnvironmentConfig
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.core.security.token.TokenClaim
import pl.dev.bkwiatkowski.core.security.token.TokenProvider
import pl.dev.bkwiatkowski.core.security.token.USER_ID_CLAIM
import pl.dev.bkwiatkowski.domain.usecase.GetMobileUserByIdUC
import pl.dev.bkwiatkowski.domain.usecase.RevokeAllMobileUserRefreshTokensUC
import pl.dev.bkwiatkowski.domain.usecase.SaveMobileUserRefreshTokenUC
import pl.dev.bkwiatkowski.domain.usecase.VerifyAndRevokeMobileUserRefreshTokenUC

class MobileRefreshTokenHandler(
  private val tokenProvider: TokenProvider,
  private val environmentConfig: EnvironmentConfig,
  private val verifyAndRevokeMobileUserRefreshTokenUC: VerifyAndRevokeMobileUserRefreshTokenUC,
  private val revokeAllMobileUserRefreshTokensUC: RevokeAllMobileUserRefreshTokensUC,
  private val saveMobileUserRefreshTokenUC: SaveMobileUserRefreshTokenUC,
  private val getMobileUserByIdUC: GetMobileUserByIdUC,
) {
  suspend fun handle(call: ApplicationCall) {
    val request = either {
      call.receiveNullable<MobileRefreshTokenRequestDto>()
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

    val userIdString = tokenProvider.verifyRefreshToken(request.refreshToken).getRightOrElse {
      call.respond(
        status = HttpStatusCode.Unauthorized,
        message = ErrorResponse(
          businessCode = "INVALID_REFRESH_TOKEN",
          message = "Refresh token is invalid or expired",
        )
      )
      return
    }

    val userId = userIdString.toIntOrNull() ?: run {
      call.respond(
        status = HttpStatusCode.Unauthorized,
        message = ErrorResponse(
          businessCode = "INVALID_REFRESH_TOKEN",
          message = "Invalid token payload",
        )
      )
      return
    }

    val verificationResult = verifyAndRevokeMobileUserRefreshTokenUC(
      params = VerifyAndRevokeMobileUserRefreshTokenUC.Params(token = request.refreshToken),
    ).getRightOrElse {
      call.respond(
        status = HttpStatusCode.Unauthorized,
        message = ErrorResponse(
          businessCode = "INVALID_REFRESH_TOKEN",
          message = "Refresh token invalid or expired.",
        )
      )
      return
    }

    if (verificationResult.isReused) {
      revokeAllMobileUserRefreshTokensUC(
        params = RevokeAllMobileUserRefreshTokensUC.Params(userId = userId)
      )
      call.respond(
        status = HttpStatusCode.Unauthorized,
        message = ErrorResponse(
          businessCode = "TOKEN_REUSED",
          message = "Refresh token reused outside of grace period. All sessions have been revoked.",
        )
      )
      return
    }

    if (userId != verificationResult.userId) {
      call.respond(
        status = HttpStatusCode.Unauthorized,
        message = ErrorResponse(
          businessCode = "INVALID_REFRESH_TOKEN",
          message = "Token mismatch",
        )
      )
      return
    }

    getMobileUserByIdUC(
      params = GetMobileUserByIdUC.Params(id = userId)
    ).getRightOrElse {
      call.respond(
        status = HttpStatusCode.Unauthorized,
        message = ErrorResponse(
          businessCode = "USER_NOT_FOUND",
          message = "User not found",
        )
      )
      return
    }

    val newAccessToken = tokenProvider.generate(
      TokenClaim(name = USER_ID_CLAIM, value = userId.toString()),
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

    val newRefreshToken = tokenProvider.generateRefreshToken(
      TokenClaim(name = USER_ID_CLAIM, value = userId.toString()),
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
        userId = userId,
        token = newRefreshToken,
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

    call.respond(
      status = HttpStatusCode.OK,
      message = MobileSignInResponseDto(
        accessToken = newAccessToken,
        refreshToken = newRefreshToken,
        expiresInSec = environmentConfig.jwtExpiresIn.inWholeSeconds,
      )
    )
  }
}
