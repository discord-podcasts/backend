package com.github.discordPodcasts.podcast

import com.github.discordPodcasts.PacketType
import com.github.discordPodcasts.podcast.events.HelloEvent
import com.github.discordPodcasts.podcast.events.encodeToBytes
import com.github.discordPodcasts.utility.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.nio.ByteBuffer
import kotlin.random.Random

class PodcastSession(
    private val podcast: Podcast,
    private val session: DefaultWebSocketServerSession
) {

    suspend fun load() {
        val isSender = session.call.request.header("isSender") == "true"
        // A sender is already connected
        if (isSender && podcast.sessions.sender.getOrNull() != null) {
            return session.closeWithError(WsError.SENDER_ALREADY_CONNECTED)
        }

        val clientAuthentication = session.getAuthDataOrThrow() ?: return

        // Sender connected
        if (isSender && podcast.senderAuthentication == clientAuthentication) onSenderConnect()
        // A receiver connected
        else onReceiverConnect()

        val connectEvent = HelloEvent(podcast.secretKey)
        val eventBytes = encodeToBytes(connectEvent)
        val bytes = ByteBuffer.allocate(1 + eventBytes.size).apply {
            put(PacketType.EVENT.raw)
            put(eventBytes)
        }.array()
        session.send(bytes)
        println("Sent hello event")

        handleClose()
    }

    private fun onSenderConnect() {
        val host = session.call.request.origin.host
        logInfo("podcast-${podcast.id}") { "Sender $host connected" }
        // Sender connected to websocket ➜ websocket is now a valid podcast
        podcast.apply {
            sessions.sender.complete(session)
            activeSince = System.currentTimeMillis()
            podcast.sessions.listen()
        }
    }

    private suspend fun onReceiverConnect() {
        val host = session.call.request.origin.host
        logInfo("podcast-${podcast.id}") { "Client $host connected" }
        podcast.sessions.addReceiver(session)
    }

    private suspend fun handleClose() {
        // Sender disconnect
        if (session == podcast.sessions.sender.getOrNull()) {
            val reason = session.closeReason.await()
            val host = session.call.request.origin.host
            logInfo("podcast-${podcast.id}") { "Sender $host disconnected: ${reason?.message ?: "No reason"}. Shutting down..." }
            podcast.destroy(WsError.SENDER_DISCONNECT) // Shutdown podcast because of missing sender
        }
        // Client disconnect
        else {
            val reason = session.closeReason.await()
            val host = session.call.request.origin.host
            logInfo("podcast-${podcast.id}") { "Client $host disconnected: ${reason?.message ?: "No reason"}" }
            podcast.sessions.removeReceiver(session)
        }
    }

}