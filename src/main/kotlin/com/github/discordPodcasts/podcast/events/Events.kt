package com.github.discordPodcasts.podcast.events

import com.github.discordPodcasts.utility.WsError
import kotlinx.serialization.Serializable

@Serializable
abstract class Event(val type: EventType)

@Serializable
data class DisconnectedEvent(
    val reason: WsError
) : Event(EventType.DISCONNECT)

@Serializable
data class HelloEvent(
    val secretKey: ByteArray
) : Event(EventType.HELLO)