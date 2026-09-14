package pl.dev.bkwiatkowski.core.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
    serialName = "java.time.LocalDateTime",
    kind = PrimitiveKind.STRING
  )

  override fun serialize(encoder: Encoder, value: LocalDateTime) {
    encoder.encodeString(value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
  }

  override fun deserialize(decoder: Decoder): LocalDateTime {
    val raw = decoder.decodeString()
    return try {
      LocalDateTime.parse(raw, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    } catch (e: DateTimeParseException) {
      OffsetDateTime.parse(raw, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDateTime()
    }
  }
}
