package pl.dev.bkwiatkowski.controller.events.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class CreateEventSessionResponse(val sessionId: String)
