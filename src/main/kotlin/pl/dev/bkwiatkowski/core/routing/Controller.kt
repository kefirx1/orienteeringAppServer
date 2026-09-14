package pl.dev.bkwiatkowski.core.routing

import io.ktor.server.routing.*

interface Controller {
  fun Route.registerRoutes()
}
