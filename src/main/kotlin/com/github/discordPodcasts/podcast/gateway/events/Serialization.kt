package com.github.discordPodcasts.podcast.gateway.events

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.encodeToJsonElement

val jsonConfiguration = Json {
    encodeDefaults = true
    explicitNulls = true
}

inline fun <reified T : Event> encodeToBytes(value: T): ByteArray {
    val string = encodeToString(value)
    return string.toByteArray()
}

inline fun <reified T : Event> encodeToString(event: T): String {
    val eventWrapper = JsonObject(
        mutableMapOf(
            "type" to JsonPrimitive(event.type.identifier),
            "content" to jsonConfiguration.encodeToJsonElement(event)
        )
    )
    return jsonConfiguration.encodeToString(eventWrapper)
}