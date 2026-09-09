package pl.dev.bkwiatkowski.controller.mobile.user.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.controller.mobile.user.dto.response.FriendDto
import pl.dev.bkwiatkowski.controller.mobile.user.dto.response.FriendshipStatusDto
import pl.dev.bkwiatkowski.controller.mobile.user.dto.response.GetFriendsListResponseDto
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.core.security.token.USER_ID_CLAIM
import pl.dev.bkwiatkowski.domain.model.FriendshipStatus
import pl.dev.bkwiatkowski.domain.usecase.GetFriendshipStatusUC
import pl.dev.bkwiatkowski.domain.usecase.GetMobileUserByIdUC
import pl.dev.bkwiatkowski.domain.usecase.GetMobileUserFriendsUC

class MobileGetFriendsListHandler(
  private val getMobileUserFriendsUC: GetMobileUserFriendsUC,
  private val getMobileUserByIdUC: GetMobileUserByIdUC,
  private val getFriendshipStatusUC: GetFriendshipStatusUC,
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

    getMobileUserFriendsUC(
      params = GetMobileUserFriendsUC.Params(userId = jwtUserId)
    ).fold(
      onRight = { friendships ->
        val friendsList = friendships.mapNotNull { friendship ->
          val friendUser = getMobileUserByIdUC(
            params = GetMobileUserByIdUC.Params(id = friendship.friendId)
          ).fold(
            onRight = { it },
            onLeft = { return@mapNotNull null }
          )

          val friendPerspectiveStatus = getFriendshipStatusUC(
            params = GetFriendshipStatusUC.Params(
              userId = friendship.friendId,
              friendId = jwtUserId,
            )
          ).fold(
            onRight = { it?.status },
            onLeft = { null }
          ) ?: return@mapNotNull null

          FriendDto(
            friendId = friendship.friendId,
            username = friendUser.username,
            createdAt = friendship.createdAt,
            status = friendship.status.toDto(),
            friendStatus = friendPerspectiveStatus.toDto(),
          )
        }

        call.respond(
          status = HttpStatusCode.OK,
          message = GetFriendsListResponseDto(friends = friendsList),
        )
      },
      onLeft = {
        call.respond(
          status = HttpStatusCode.InternalServerError,
          message = ErrorResponse(
            businessCode = "ERROR_FETCHING_FRIENDS",
            message = "Nie udało się pobrać listy znajomych",
            showMessage = true,
          )
        )
      }
    )
  }

  private fun FriendshipStatus.toDto(): FriendshipStatusDto =
    when (this) {
      FriendshipStatus.ACCEPTED -> FriendshipStatusDto.ACCEPTED
      FriendshipStatus.NOT_ACCEPTED -> FriendshipStatusDto.NOT_ACCEPTED
    }
}
