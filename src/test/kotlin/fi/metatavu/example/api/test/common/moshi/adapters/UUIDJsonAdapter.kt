package fi.metatavu.example.api.test.common.moshi.adapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.util.*

/**
 * Adapter class for UUID
 *
 * @author Jari Nykänen
 */
class UUIDJsonAdapter {

    @ToJson
    fun toJson(value: UUID?) = value?.toString()

    @FromJson
    fun fromJson(input: String) = UUID.fromString(input)
}
