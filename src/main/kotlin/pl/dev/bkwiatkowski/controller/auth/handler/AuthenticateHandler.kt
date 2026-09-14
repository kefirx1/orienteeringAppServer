package pl.dev.bkwiatkowski.controller.auth.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

class AuthenticateHandler {
  suspend fun handle(call: ApplicationCall) {
    call.respond(HttpStatusCode.OK)
  }
}