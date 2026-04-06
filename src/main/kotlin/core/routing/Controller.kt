package pl.dev.bkwiatkowski.core.routing

import io.ktor.server.routing.Routing

interface Controller {
  fun Routing.registerRoutes()
}