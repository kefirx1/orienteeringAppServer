package pl.dev.bkwiatkowski.controller.mobile.settings.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.controller.mobile.settings.dto.response.MobileSettingsResponseDto
import java.time.LocalDateTime

class MobileSettingsHandler {
  suspend fun handle(call: ApplicationCall) {
    val localDateTime = LocalDateTime.now()

    call.respond(
      status = HttpStatusCode.OK,
      message = MobileSettingsResponseDto(
        serverLocalDateTime = localDateTime,
      )
    )
  }
}
