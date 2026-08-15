package pl.dev.bkwiatkowski.controller.events.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.core.EnvironmentConfig
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.core.security.token.USER_ID_CLAIM
import pl.dev.bkwiatkowski.data.repository.EventRepository
import pl.dev.bkwiatkowski.domain.model.AdminPanelUser
import pl.dev.bkwiatkowski.domain.usecase.GetAdminPanelUserByIdUC
import pl.dev.bkwiatkowski.domain.usecase.GetEventByIdUC
import java.io.File

class EventImageHandler(
  private val environmentConfig: EnvironmentConfig,
  private val getAdminPanelUserByIdUC: GetAdminPanelUserByIdUC,
  private val getEventByIdUC: GetEventByIdUC,
  private val eventRepository: EventRepository,
) {
  suspend fun handle(call: ApplicationCall) {
    val principal = call.principal<JWTPrincipal>()
    val userId = principal?.payload?.getClaim(USER_ID_CLAIM)?.asString()?.toIntOrNull()

    if (userId == null) {
      call.respond(
        status = HttpStatusCode.Unauthorized,
        message = ErrorResponse(
          businessCode = "UNAUTHORIZED",
          message = "User is not authenticated",
        ),
      )
      return
    }

    val relativePath = call.parameters["path"] ?: run {
      call.respond(HttpStatusCode.BadRequest)
      return
    }

    val sessionUuid = relativePath.substringBefore('/', missingDelimiterValue = "")
    if (sessionUuid.isBlank()) {
      call.respond(HttpStatusCode.BadRequest)
      return
    }

    val session = eventRepository.getSessionByUuid(sessionUuid).getRightOrNull() ?: run {
      call.respond(HttpStatusCode.NotFound)
      return
    }

    val event = getEventByIdUC(
      params = GetEventByIdUC.Params(eventId = session.eventId),
    ).getRightOrNull() ?: run {
      call.respond(HttpStatusCode.NotFound)
      return
    }

    val user = getAdminPanelUserByIdUC(params = GetAdminPanelUserByIdUC.Params(id = userId)).getRightOrNull() ?: run {
      call.respond(
        status = HttpStatusCode.Forbidden,
        message = ErrorResponse(
          businessCode = "FORBIDDEN",
          message = "User not found",
        ),
      )
      return
    }

    val isAdmin = user.role == AdminPanelUser.Role.ADMIN
    val isOwner = event.userId == userId

    if (!isAdmin && !isOwner) {
      call.respond(
        status = HttpStatusCode.Forbidden,
        message = ErrorResponse(
          businessCode = "ACCESS_FORBIDDEN",
          message = "You do not have permission to access this image",
        ),
      )
      return
    }

    val storageDir = File(environmentConfig.imagesStorageDir)
    val target = File(storageDir, relativePath)

    if (!target.exists() || !target.canonicalPath.startsWith(storageDir.canonicalPath)) {
      call.respond(HttpStatusCode.NotFound)
      return
    }

    val extension = target.extension.lowercase()
    if (extension !in listOf("jpg", "jpeg")) {
      call.respond(
        status = HttpStatusCode.UnsupportedMediaType,
        message = ErrorResponse(
          businessCode = "UNSUPPORTED_MEDIA_TYPE",
          message = "Only JPG/JPEG images are supported",
        ),
      )
      return
    }

    call.response.header(HttpHeaders.ContentType, ContentType.Image.JPEG.toString())
    call.respondFile(target)
  }
}
