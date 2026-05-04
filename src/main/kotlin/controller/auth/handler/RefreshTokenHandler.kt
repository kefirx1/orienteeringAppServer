package pl.dev.bkwiatkowski.controller.auth.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.controller.auth.dto.response.SignInResponseDto
import pl.dev.bkwiatkowski.core.EnvironmentConfig
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.core.security.token.TokenClaim
import pl.dev.bkwiatkowski.core.security.token.TokenProvider
import pl.dev.bkwiatkowski.core.security.token.USER_ID_CLAIM
import pl.dev.bkwiatkowski.core.security.token.USER_ROLE_CLAIM
import pl.dev.bkwiatkowski.domain.usecase.GetAdminPanelUserByIdUC
import pl.dev.bkwiatkowski.domain.usecase.RevokeAllUserRefreshTokensUC
import pl.dev.bkwiatkowski.domain.usecase.SaveRefreshTokenUC
import pl.dev.bkwiatkowski.domain.usecase.VerifyAndRevokeRefreshTokenUC

class RefreshTokenHandler(
  private val tokenProvider: TokenProvider,
  private val environmentConfig: EnvironmentConfig,
  private val verifyAndRevokeRefreshTokenUC: VerifyAndRevokeRefreshTokenUC,
  private val revokeAllUserRefreshTokensUC: RevokeAllUserRefreshTokensUC,
  private val saveRefreshTokenUC: SaveRefreshTokenUC,
  private val getAdminPanelUserByIdUC: GetAdminPanelUserByIdUC
) {
  suspend fun handle(call: ApplicationCall) {
    val refreshToken = either {
      call.request.cookies[REFRESH_TOKEN_COOKIE_NAME]
    }.getRightOrNull() ?: run {
      call.respond(
        status = HttpStatusCode.BadRequest,
        message = ErrorResponse(
          businessCode = "INVALID_REQUEST",
          message = "Invalid or missing request body"
        )
      )
      return
    }

    val userIdString = tokenProvider.verifyRefreshToken(refreshToken).getRightOrElse {
      call.respond(
        status = HttpStatusCode.Unauthorized,
        message = ErrorResponse(
          businessCode = "INVALID_REFRESH_TOKEN",
          message = "Refresh token is invalid or expired"
        )
      )
      return
    }

    val userId = userIdString.toIntOrNull() ?: run {
      call.respond(
        status = HttpStatusCode.Unauthorized,
        message = ErrorResponse(
          businessCode = "INVALID_REFRESH_TOKEN",
          message = "Invalid token payload"
        )
      )
      return
    }
    
    val verificationResult = verifyAndRevokeRefreshTokenUC(
      params = VerifyAndRevokeRefreshTokenUC.Params(
        token = refreshToken,
      )
    ).getRightOrElse {
      call.respond(
        status = HttpStatusCode.Unauthorized,
        message = ErrorResponse(
          businessCode = "INVALID_REFRESH_TOKEN",
          message = "Refresh token invalid or expired."
        )
      )
      return
    }

    if (verificationResult.isReused) {
      revokeAllUserRefreshTokensUC(
        params = RevokeAllUserRefreshTokensUC.Params(
          userId = userId
        )
      )

      call.respond(
        status = HttpStatusCode.Unauthorized,
        message = ErrorResponse(
          businessCode = "TOKEN_REUSED",
          message = "Refresh token reused outside of grace period. All sessions have been revoked."
        )
      )
      return
    }

    if (userId != verificationResult.userId) {
      call.respond(
        status = HttpStatusCode.Unauthorized,
        message = ErrorResponse(
          businessCode = "INVALID_REFRESH_TOKEN",
          message = "Token mismatch"
        )
      )
      return
    }

    val user = getAdminPanelUserByIdUC(
      params = GetAdminPanelUserByIdUC.Params(id = userId)
    ).getRightOrElse {
      call.respond(
        status = HttpStatusCode.Unauthorized,
        message = ErrorResponse(
          businessCode = "USER_NOT_FOUND",
          message = "User not found"
        )
      )
      return
    }

    val token = tokenProvider.generate(
      TokenClaim(
        name = USER_ID_CLAIM,
        value = userId.toString(),
      ),
      TokenClaim(
        name = USER_ROLE_CLAIM,
        value = user.role.name,
      )
    ).getRightOrElse {
      call.respond(
        status = HttpStatusCode.InternalServerError,
        message = ErrorResponse(
          businessCode = "TOKEN_GENERATION_ERROR",
          message = "Failed to generate authentication token"
        )
      )
      return
    }

    val newRefreshToken = tokenProvider.generateRefreshToken(
      TokenClaim(
        name = USER_ID_CLAIM,
        value = userId.toString(),
      ),
      TokenClaim(
        name = USER_ROLE_CLAIM,
        value = user.role.name,
      )
    ).getRightOrElse {
      call.respond(
        status = HttpStatusCode.InternalServerError,
        message = ErrorResponse(
          businessCode = "TOKEN_GENERATION_ERROR",
          message = "Failed to generate refresh token"
        )
      )
      return
    }

    saveRefreshTokenUC(
      params = SaveRefreshTokenUC.Params(
        userId = userId, 
        token = newRefreshToken
      )
    ).getRightOrElse {
      call.respond(
        status = HttpStatusCode.InternalServerError,
        message = ErrorResponse(
          businessCode = "DB_SAVE_ERROR",
          message = "Failed to save refresh token in the database"
        )
      )
      return
    }

    call.response.cookies.append(
      name = REFRESH_TOKEN_COOKIE_NAME,
      value = newRefreshToken,
      maxAge = environmentConfig.jwtRefreshExpiresIn.inWholeSeconds,
      httpOnly = true,
      secure = true,
      path = "/api/auth/refresh",
      extensions = mapOf("SameSite" to "Strict")
    )

    call.respond(
      status = HttpStatusCode.OK,
      message = SignInResponseDto(
        token = token,
        expiresInSec = environmentConfig.jwtExpiresIn.inWholeSeconds,
        role = user.role,
      )
    )
  }
}

private const val REFRESH_TOKEN_COOKIE_NAME = "refreshToken"
