package com.github.discordPodcasts.podcast.gateway.events

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
open class Event(
    @Transient
    val type: EventType = EventType.UNKNOWN
)