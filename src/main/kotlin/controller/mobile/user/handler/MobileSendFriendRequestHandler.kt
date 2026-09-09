package pl.dev.bkwiatkowski.controller.mobile.user.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.controller.mobile.user.dto.request.SendFriendRequestDto
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.core.security.token.USER_ID_CLAIM
import pl.dev.bkwiatkowski.domain.usecase.SendMobileUserFriendRequestUC

class MobileSendFriendRequestHandler(
  private val sendMobileUserFriendRequestUC: SendMobileUserFriendRequestUC,
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
      call.receive<SendFriendRequestDto>()
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

    if (requestDto.friendId == jwtUserId) {
      call.respond(
        status = HttpStatusCode.BadRequest,
        message = ErrorResponse(
          businessCode = "INVALID_REQUEST",
          message = "Cannot send friend request to yourself",
        )
      )
      return
    }

    sendMobileUserFriendRequestUC(
      params = SendMobileUserFriendRequestUC.Params(
        userId = jwtUserId,
        friendId = requestDto.friendId,
      )
    ).fold(
      onRight = {
        call.respond(HttpStatusCode.Created)
      },
      onLeft = {
        call.respond(
          status = HttpStatusCode.NotFound,
          message = ErrorResponse(
            businessCode = "USER_NOT_FOUND",
            message = "Friend user not found",
          )
        )
      }
    )
  }
}
