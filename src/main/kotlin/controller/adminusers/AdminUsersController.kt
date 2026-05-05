package pl.dev.bkwiatkowski.controller.adminusers

import io.ktor.server.auth.*
import io.ktor.server.routing.*
import pl.dev.bkwiatkowski.controller.adminusers.handler.AddAdminUserHandler
import pl.dev.bkwiatkowski.controller.adminusers.handler.DeleteAdminUserHandler
import pl.dev.bkwiatkowski.controller.adminusers.handler.GetAllAdminUsersHandler
import pl.dev.bkwiatkowski.core.routing.Controller

class AdminUsersController(
  private val getAllAdminUsersHandler: GetAllAdminUsersHandler,
  private val deleteAdminUserHandler: DeleteAdminUserHandler,
  private val addAdminUserHandler: AddAdminUserHandler,
) : Controller {

  override fun Route.registerRoutes() {
    authenticate {
      route("/admin/users") {
        get {
          getAllAdminUsersHandler.handle(call)
        }

        post {
          addAdminUserHandler.handle(call)
        }

        delete("{id}") {
          deleteAdminUserHandler.handle(call)
        }
      }
    }
  }
}
