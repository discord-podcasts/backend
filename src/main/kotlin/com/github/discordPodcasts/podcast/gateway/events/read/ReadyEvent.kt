package com.github.discordPodcasts.podcast.gateway.events.read

import com.github.discordPodcasts.podcast.gateway.events.Event
import com.github.discordPodcasts.podcast.gateway.events.EventType
import kotlinx.serialization.Serializable

@Serializable
data class ReadyEvent(
    val ip: String,
    val port: Int
) : Event(EventType.IDENTIFY)