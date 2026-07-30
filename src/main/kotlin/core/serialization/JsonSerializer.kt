package pl.dev.bkwiatkowski.core.serialization

import kotlinx.serialization.KSerializer

interface JsonSerializer {
  fun <T> serialize(value: T, serializer: KSerializer<T>): String
  fun <T> deserialize(serialized: String, serializer: KSerializer<T>): T
}
