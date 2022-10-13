package com.github.discordPodcasts.podcast.events

import com.github.discordPodcasts.utility.WsError
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
abstract class Event(
    @Transient
    val type: EventType = EventType.UNKNOWN
)

@Serializable
data class DisconnectedEvent(
    val reason: WsError
) : Event(EventType.DISCONNECT)

@Serializable
data class HelloEvent(
    val secretKey: ByteArray
) : Event(EventType.HELLO)