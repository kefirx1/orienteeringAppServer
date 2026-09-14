package pl.dev.bkwiatkowski.controller.mobile.user.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.controller.mobile.user.dto.request.RemoveFriendDto
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.core.security.token.USER_ID_CLAIM
import pl.dev.bkwiatkowski.domain.usecase.RemoveMobileUserFriendUC

class MobileRemoveFriendHandler(
  private val removeMobileUserFriendUC: RemoveMobileUserFriendUC,
) {
  suspend fun handle(call: ApplicationCall) {
    val principal = call.principal<JWTPrincipal>()
    val jwtUserId = principal?.payload?.getClaim(USER_ID_CLAIM)?.asString()?.toIntOrNull()

    if (jwtUserId == null) {
      call.respond(
        status = HttpStatusCode.Unauthorized,
        message = ErrorResponse(
          businessCode = "UNAUTHORIZED",
          message = "User is not authenticated",
        )
      )
      return
    }

    val requestDto = either {
      call.receive<RemoveFriendDto>()
    }.getRightOrNull() ?: run {
      call.respond(
        status = HttpStatusCode.BadRequest,
        message = ErrorResponse(
          businessCode = "INVALID_REQUEST",
          message = "Invalid request body",
        )
      )
      return
    }

    removeMobileUserFriendUC(
      params = RemoveMobileUserFriendUC.Params(
        userId = jwtUserId,
        friendId = requestDto.friendId,
      )
    ).fold(
      onRight = {
        call.respond(HttpStatusCode.OK)
      },
      onLeft = {
        call.respond(
          status = HttpStatusCode.InternalServerError,
          message = ErrorResponse(
            businessCode = "ERROR_REMOVING_FRIEND",
            message = "Failed to remove friend",
          )
        )
      }
    )
  }
}
