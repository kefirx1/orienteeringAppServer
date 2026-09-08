package pl.dev.bkwiatkowski.controller.mobile.events.handler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.core.response.ErrorResponse
import pl.dev.bkwiatkowski.controller.mobile.events.dto.response.UploadImageResponse
import pl.dev.bkwiatkowski.controller.mobile.events.dto.request.UploadImageRequest
import pl.dev.bkwiatkowski.core.security.coder.ByteCoder
import pl.dev.bkwiatkowski.core.security.token.USER_ID_CLAIM
import pl.dev.bkwiatkowski.domain.usecase.StoreSessionImageUC
import pl.dev.bkwiatkowski.controller.mobile.events.MobileEventConstants

class MobileUploadImageHandler(
  private val storeSessionImageUC: StoreSessionImageUC,
  private val byteCoder: ByteCoder,
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
        )
      )
      return
    }

    val sessionUuid = call.parameters["sessionUuid"]
    if (sessionUuid.isNullOrBlank()) {
      call.respond(
        status = HttpStatusCode.BadRequest,
        message = ErrorResponse(
          businessCode = "MISSING_SESSION_UUID",
          message = "Missing sessionUuid parameter"
        )
      )
      return
    }

    val request = either {
      call.receive<UploadImageRequest>()
    }.getRightOrNull() ?: run {
      call.respond(
        status = HttpStatusCode.BadRequest,
        message = ErrorResponse(
          businessCode = "INVALID_BODY",
          message = "Failed to parse request body as UploadImageRequest",
        )
      )
      return
    }

    val bytes = byteCoder.decode(encodedString = request.image).getRightOrNull() ?: run {
      call.respond(
        status = HttpStatusCode.BadRequest,
        message = ErrorResponse(
          businessCode = "INVALID_BASE64",
          message = "Failed to decode base64 image string",
        )
      )
      return
    }

    if (bytes.size > MobileEventConstants.MAX_IMAGE_SIZE_BYTES) {
      call.respond(
        status = HttpStatusCode.BadRequest,
        message = ErrorResponse(
          showMessage = true,
          businessCode = "IMAGE_TOO_LARGE",
          message = "Rozmiar zdjęcia przekracza ${MobileEventConstants.MAX_IMAGE_SIZE_BYTES / 1024}KB",
        )
      )
      return
    }

    val isJpg = bytes.size >= 2 && bytes[0] == 0xFF.toByte() && bytes[1] == 0xD8.toByte()
    if (!isJpg) {
      call.respond(
        status = HttpStatusCode.BadRequest,
        message = ErrorResponse(
          businessCode = "ONLY_JPG_ALLOWED",
          message = "Only JPEG images are allowed",
        )
      )
      return
    }

    storeSessionImageUC(
      params = StoreSessionImageUC.Params(
        imageBytes = bytes,
        sessionUuid = sessionUuid,
      )
    ).fold(
      onRight = { path ->
        call.respond(
          status = HttpStatusCode.OK,
          message = UploadImageResponse(path = path),
        )
      },
      onLeft = { error ->
        call.respond(
          status = HttpStatusCode.InternalServerError,
          message = ErrorResponse(
            businessCode = "STORE_FAILED",
            message = "Failed to store file",
          )
        )
      }
    )
  }
}
