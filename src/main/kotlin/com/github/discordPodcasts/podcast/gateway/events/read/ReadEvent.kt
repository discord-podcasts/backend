package com.github.discordPodcasts.podcast.gateway.events.read

import com.github.discordPodcasts.podcast.gateway.events.Event
import com.github.discordPodcasts.podcast.gateway.events.EventType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement

@Serializable
data class ReadEvent(
    val type: EventType,
    val content: JsonObject
) {
    inline fun <reified T : Event> interpret(json: Json): T {
        return json.decodeFromJsonElement(content)
    }
}