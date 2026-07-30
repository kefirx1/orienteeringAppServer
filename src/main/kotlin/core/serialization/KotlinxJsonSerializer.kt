package pl.dev.bkwiatkowski.core.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual

class KotlinxJsonSerializer : JsonSerializer {
  internal val json: Json = Json {
    serializersModule = SerializersModule {
      contextual(LocalDateTimeSerializer)
      contextual(LocalDateSerializer)
    }
    isLenient = true
    ignoreUnknownKeys = true
  }

  override fun <T> serialize(value: T, serializer: KSerializer<T>): String =
    json.encodeToString(serializer = serializer, value = value)

  override fun <T> deserialize(serialized: String, serializer: KSerializer<T>): T =
    json.decodeFromString(deserializer = serializer, string = serialized)
}
