package pl.dev.bkwiatkowski.controller.auth

import io.ktor.server.auth.*
import io.ktor.server.routing.*
import pl.dev.bkwiatkowski.controller.auth.handler.AuthenticateHandler
import pl.dev.bkwiatkowski.controller.auth.handler.SignInHandler
import pl.dev.bkwiatkowski.controller.auth.handler.SignUpHandler
import pl.dev.bkwiatkowski.core.routing.Controller

class AuthController(
  private val authenticateHandler: AuthenticateHandler,
  private val signUpHandler: SignUpHandler,
  private val signInHandler: SignInHandler,
) : Controller {

  override fun Route.registerRoutes() {
    authenticate {
      get("authenticate") {
        authenticateHandler.handle(call)
      }
    }

    route("/auth") {
      post("/signup") {
        signUpHandler.handle(call)
      }

      post("/signin") {
        signInHandler.handle(call)
      }
    }
  }
}
