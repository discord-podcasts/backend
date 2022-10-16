package com.github.discordPodcasts.podcast.gateway.events.read

import com.github.discordPodcasts.podcast.gateway.events.Event
import com.github.discordPodcasts.podcast.gateway.events.EventType
import kotlinx.serialization.Serializable

@Serializable
data class IdentifyEvent(
    val host: Boolean,
    val clientId: String,
    val token: String
) : Event(EventType.IDENTIFY)