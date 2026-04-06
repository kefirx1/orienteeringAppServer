package pl.dev.bkwiatkowski.controller.auth.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.controller.auth.dto.request.SignInRequestDto
import pl.dev.bkwiatkowski.controller.auth.dto.response.SignInResponseDto
import pl.dev.bkwiatkowski.core.security.token.TokenClaim
import pl.dev.bkwiatkowski.core.security.token.TokenProvider
import pl.dev.bkwiatkowski.core.security.token.USER_ID_CLAIM
import pl.dev.bkwiatkowski.domain.model.SaltedHash
import pl.dev.bkwiatkowski.domain.usecase.GetAdminPanelUserUC
import pl.dev.bkwiatkowski.domain.usecase.VerifyAdminPanelUserAuthenticationUC

class SignInHandler(
  private val getAdminPanelUserUC: GetAdminPanelUserUC,
  private val verifyAdminPanelUserAuthenticationUC: VerifyAdminPanelUserAuthenticationUC,
  private val tokenProvider: TokenProvider,
) {
  suspend fun handle(call: ApplicationCall) {
    val request = runCatching { call.receiveNullable<SignInRequestDto>() }.getOrNull() ?: run {
      call.respond(HttpStatusCode.BadRequest)
      return
    }

    val user = getAdminPanelUserUC(
      params = GetAdminPanelUserUC.Params(
        username = request.username,
      ),
    ).getRightOrElse {
      call.respond(HttpStatusCode.Conflict)
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
      call.respond(HttpStatusCode.Conflict)
      return
    }

    if (result == VerifyAdminPanelUserAuthenticationUC.Result.InvalidCredentials) {
      call.respond(HttpStatusCode.Conflict)
      return
    }

    val token = tokenProvider.generate(
      TokenClaim(
        name = USER_ID_CLAIM,
        value = user.id.toString(),
      )
    ).getRightOrElse {
      call.respond(HttpStatusCode.InternalServerError)
      return
    }

    call.respond(
      status = HttpStatusCode.OK,
      message = SignInResponseDto(
        token = token,
      ),
    )
  }
}