package pl.dev.bkwiatkowski.controller.auth.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.controller.auth.dto.request.SignInRequestDto
import pl.dev.bkwiatkowski.controller.auth.dto.response.SignInResponseDto
import pl.dev.bkwiatkowski.core.EnvironmentConfig
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.core.security.token.TokenClaim
import pl.dev.bkwiatkowski.core.security.token.TokenProvider
import pl.dev.bkwiatkowski.core.security.token.USER_ID_CLAIM
import pl.dev.bkwiatkowski.core.security.token.USER_ROLE_CLAIM
import pl.dev.bkwiatkowski.domain.model.SaltedHash
import pl.dev.bkwiatkowski.domain.usecase.GetAdminPanelUserUC
import pl.dev.bkwiatkowski.domain.usecase.SaveRefreshTokenUC
import pl.dev.bkwiatkowski.domain.usecase.VerifyAdminPanelUserAuthenticationUC

class SignInHandler(
  private val getAdminPanelUserUC: GetAdminPanelUserUC,
  private val verifyAdminPanelUserAuthenticationUC: VerifyAdminPanelUserAuthenticationUC,
  private val tokenProvider: TokenProvider,
  private val environmentConfig: EnvironmentConfig,
  private val saveRefreshTokenUC: SaveRefreshTokenUC
) {
  suspend fun handle(call: ApplicationCall) {
    val request = runCatching { call.receiveNullable<SignInRequestDto>() }.getOrNull() ?: run {
      call.respond(
        status = HttpStatusCode.BadRequest,
        message = ErrorResponse(
          businessCode = "INVALID_REQUEST",
          message = "Invalid or missing request body"
        )
      )
      return
    }

    val user = getAdminPanelUserUC(
      params = GetAdminPanelUserUC.Params(
        username = request.username,
      ),
    ).getRightOrElse {
      call.respond(
        status = HttpStatusCode.NotFound,
        message = ErrorResponse(
          businessCode = "USER_NOT_FOUND",
          message = "User does not exist"
        )
      )
      return
    }

    val result = verifyAdminPanelUserAuthenticationUC(
      params = VerifyAdminPanelUserAuthenticationUC.Params(
        username = request.username,
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
          message = "Failed to verify authentication"
        )
      )
      return
    }

    if (result == VerifyAdminPanelUserAuthenticationUC.Result.InvalidCredentials) {
      call.respond(
        status = HttpStatusCode.Unauthorized,
        message = ErrorResponse(
          businessCode = "INVALID_CREDENTIALS",
          message = "Invalid username or password"
        )
      )
      return
    }

    val token = tokenProvider.generate(
      TokenClaim(
        name = USER_ID_CLAIM,
        value = user.id.toString(),
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

    val refreshToken = tokenProvider.generateRefreshToken(
      TokenClaim(
        name = USER_ID_CLAIM,
        value = user.id.toString(),
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
        userId = user.id, 
        token = refreshToken
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
      value = refreshToken,
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
