package com.github.discordPodcasts.podcast.gateway.events.write

import com.github.discordPodcasts.podcast.gateway.events.EventType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class WriteEvent(
    val type: EventType,
    val content: JsonObject
)