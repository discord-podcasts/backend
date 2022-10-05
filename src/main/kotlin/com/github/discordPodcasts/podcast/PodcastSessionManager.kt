package com.github.discordPodcasts.podcast

import com.github.discordPodcasts.PacketType
import com.github.discordPodcasts.podcast.events.DisconnectedEvent
import com.github.discordPodcasts.podcast.events.encodeToBytes
import com.github.discordPodcasts.utility.WsError
import com.github.discordPodcasts.utility.getOrNull
import io.ktor.server.websocket.*
import io.ktor.util.*
import io.ktor.websocket.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.nio.ByteBuffer

class PodcastSessionManager(
    val podcast: Podcast,
    var sender: CompletableDeferred<DefaultWebSocketServerSession> = CompletableDeferred(),
    val receivers: MutableList<DefaultWebSocketServerSession> = mutableListOf()
) {
    private val minFrameBytesSize = 1 + 24 // Is audio + nonce

    suspend fun createSession(session: DefaultWebSocketServerSession) {
        PodcastSession(podcast, session).load()
    }

    /**
     * Start listening to the senders input and [broadcast] it to the senders.
     */
    fun listen() = podcast.coroutineScope.launch {
        sender.getOrNull()?.incoming?.receiveAsFlow()?.collect {
            // Wrong payload
            if (it.data.size <= minFrameBytesSize) return@collect podcast.destroy(WsError.INVALID_PAYLOAD)

            /*
            val bytes = ByteReadPacket(it.buffer)
            val isAudio = bytes.readBytes(1).first() == 0.toByte()
            val nonce = bytes.readBytes(24)
            */
            broadcast(it.data)
        } ?: throw IllegalStateException("Sender is not initialized")
    }

    /**
     * Sends the given byte array to all [receivers].
     *
     * @param bytes The byte array to send.
     */
    private suspend fun broadcast(bytes: ByteArray) {
        receivers.forEach { it.send(bytes) }
    }

    /**
     * Used to disconnect all receivers and the sender.
     * Used in [Podcast.destroy].
     *
     * @param reason The close reason.
     */
    suspend fun disconnectAll(reason: WsError) {
        val event = DisconnectedEvent(reason)
        val eventAsBytes = encodeToBytes(event)

        // Give client disconnect information
        val bytes = ByteBuffer.allocate(1 + eventAsBytes.size).apply {
            put(PacketType.EVENT.raw)
            put(eventAsBytes)
        }.moveToByteArray()
        broadcast(bytes)
        receivers.forEach { it.close() }
        receivers.clear()

        // Give sender close information
        sender.getOrNull()?.apply {
            send(eventAsBytes)
            close(reason.asCloseReason)
        }
    }

}