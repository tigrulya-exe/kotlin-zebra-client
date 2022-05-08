package ru.nsu.manasyan.zebra.util

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

inline fun <reified T> Gson.fromJson(json: String) = fromJson<T>(json, object : TypeToken<T>() {}.type)

val INSTANT_FORMATTER: DateTimeFormatter = DateTimeFormatter
    .ISO_LOCAL_DATE_TIME
    .withZone(ZoneId.from(ZoneOffset.UTC))

class InstantJsonAdapter : JsonSerializer<Instant>, JsonDeserializer<Instant> {
    override fun serialize(src: Instant?, typeOfSrc: Type?, context: JsonSerializationContext?) =
        src?.let { JsonPrimitive(formatInstant(src)) }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?) =
        json?.let { INSTANT_FORMATTER.parse(json.asString, Instant::from) }
}

fun formatInstant(input: Instant): String = INSTANT_FORMATTER.format(input)