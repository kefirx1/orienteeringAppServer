package pl.dev.bkwiatkowski.controller.maps

import io.ktor.server.auth.*
import io.ktor.server.routing.*
import pl.dev.bkwiatkowski.controller.maps.handler.AddMapHandler
import pl.dev.bkwiatkowski.controller.maps.handler.DeleteMapHandler
import pl.dev.bkwiatkowski.controller.maps.handler.MapDetailHandler
import pl.dev.bkwiatkowski.controller.maps.handler.MapListHandler
import pl.dev.bkwiatkowski.core.routing.Controller

class MapController(
  private val mapListHandler: MapListHandler,
  private val mapDetailHandler: MapDetailHandler,
  private val addMapHandler: AddMapHandler,
  private val deleteMapHandler: DeleteMapHandler,
) : Controller {

  override fun Route.registerRoutes() {
    authenticate {
      route("/maps") {
        get {
          mapListHandler.handle(call)
        }

        post {
          addMapHandler.handle(call)
        }

        get("{id}") {
          mapDetailHandler.handle(call)
        }

        delete("{id}") {
          deleteMapHandler.handle(call)
        }
      }
    }
  }
}
