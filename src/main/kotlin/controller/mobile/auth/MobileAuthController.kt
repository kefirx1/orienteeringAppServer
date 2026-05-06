package pl.dev.bkwiatkowski.controller.mobile.auth

import io.ktor.server.routing.*
import pl.dev.bkwiatkowski.controller.mobile.auth.handler.MobileRefreshTokenHandler
import pl.dev.bkwiatkowski.controller.mobile.auth.handler.MobileSignInHandler
import pl.dev.bkwiatkowski.controller.mobile.auth.handler.MobileSignUpHandler
import pl.dev.bkwiatkowski.core.routing.Controller

class MobileAuthController(
  private val mobileSignUpHandler: MobileSignUpHandler,
  private val mobileSignInHandler: MobileSignInHandler,
  private val mobileRefreshTokenHandler: MobileRefreshTokenHandler,
) : Controller {

  override fun Route.registerRoutes() {
    route("/mobile/auth") {
      post("/register") {
        mobileSignUpHandler.handle(call)
      }

      post("/login") {
        mobileSignInHandler.handle(call)
      }

      post("/refresh") {
        mobileRefreshTokenHandler.handle(call)
      }
    }
  }
}
