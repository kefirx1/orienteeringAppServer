package pl.dev.bkwiatkowski.controller.mobile.user

import io.ktor.server.auth.*
import io.ktor.server.routing.*
import pl.dev.bkwiatkowski.controller.mobile.user.handler.*
import pl.dev.bkwiatkowski.core.routing.Controller

class MobileUserController(
  private val getUserSessionsHandler: MobileGetUserSessionsSummaryHandler,
  private val checkUserExistsHandler: MobileCheckUserHandler,
  private val getFriendsListHandler: MobileGetFriendsListHandler,
  private val sendFriendRequestHandler: MobileSendFriendRequestHandler,
  private val acceptFriendRequestHandler: MobileAcceptFriendRequestHandler,
  private val removeFriendHandler: MobileRemoveFriendHandler,
) : Controller {

  override fun Route.registerRoutes() {
    authenticate {
      route("/mobile/user") {
        get("/by-username/{username}") {
          checkUserExistsHandler.handle(call)
        }

        get("{userId}/sessions") {
          getUserSessionsHandler.handle(call)
        }

        get("/friends/list") {
          getFriendsListHandler.handle(call)
        }

        post("/friends/send-request") {
          sendFriendRequestHandler.handle(call)
        }

        post("/friends/accept-request") {
          acceptFriendRequestHandler.handle(call)
        }

        delete("/friends/remove") {
          removeFriendHandler.handle(call)
        }
      }
    }
  }
}
