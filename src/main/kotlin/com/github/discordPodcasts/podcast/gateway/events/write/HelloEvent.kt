package com.github.discordPodcasts.podcast.gateway.events.write

import com.github.discordPodcasts.podcast.gateway.events.Event
import com.github.discordPodcasts.podcast.gateway.events.EventType
import kotlinx.serialization.Serializable

@Serializable
data class HelloEvent(
    val ip: String,
    val port: Int,
    val secretKey: List<Byte>
):Event(EventType.HELLO)