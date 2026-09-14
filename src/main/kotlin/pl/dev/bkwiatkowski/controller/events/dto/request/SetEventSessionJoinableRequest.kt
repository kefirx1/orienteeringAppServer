package pl.dev.bkwiatkowski.controller.events.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class SetEventSessionJoinableRequest(val userCanJoin: Boolean)
