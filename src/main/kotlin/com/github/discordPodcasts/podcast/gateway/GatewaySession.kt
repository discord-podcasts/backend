package com.github.discordPodcasts.podcast.gateway

import accounts
import com.github.discordPodcasts.podcast.audioManager.AudioManager
import com.github.discordPodcasts.podcast.gateway.events.Event
import com.github.discordPodcasts.podcast.gateway.events.EventType
import com.github.discordPodcasts.podcast.gateway.events.read.IdentifyEvent
import com.github.discordPodcasts.podcast.gateway.events.read.ReadEvent
import com.github.discordPodcasts.podcast.gateway.events.read.ReadyEvent
import com.github.discordPodcasts.podcast.gateway.events.write.HelloEvent
import com.github.discordPodcasts.podcast.gateway.events.write.WriteEvent
import com.github.discordPodcasts.utility.Authentication
import io.ktor.network.sockets.*
import io.ktor.util.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import org.slf4j.LoggerFactory

class GatewaySession(
    private val session: Socket,
    private val audioManager: AudioManager,
    private val coroutineScope: CoroutineScope
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    var authentication: Authentication? = null
    var claimsHost = false
    var audioSocketAddress: InetSocketAddress? = null
    private val readChannel = session.openReadChannel()
    private val writeChannel = session.openWriteChannel(true)
    private val json = Json { ignoreUnknownKeys = true }

    init {
        logger.info("Client connected to gateway")

        coroutineScope.launch {
            readChannel.consumeEachBufferRange { buffer, _ ->
                handleIncome(buffer.moveToByteArray())
                true
            }
        }
    }

    private suspend fun handleIncome(bytes: ByteArray) {
        val raw = ByteReadPacket(bytes).readText()
        val event = runCatching { json.decodeFromString<ReadEvent>(raw) }.getOrNull() ?: return

        when (event.type) {
            EventType.IDENTIFY -> clientIdentify(event.interpret(json))
            EventType.READY    -> clientReady(event.interpret(json))
            else               -> throw Exception("Unknown event")
        }
    }

    private suspend inline fun <reified T : Event> send(event: T) {
        val content = json.encodeToJsonElement(event).jsonObject
        val event = WriteEvent(event.type, content)
        val bytes = json.encodeToString(event).toByteArray()
        writeChannel.writeFully(bytes, 0, bytes.size)
    }

    private suspend fun clientIdentify(event: IdentifyEvent) {
        if (accounts[event.clientId] != event.token) closeConnection()

        authentication = Authentication(event.clientId, event.token)
        claimsHost = event.host

        val audioSocketAddress = audioManager.socketAddress
        val helloEvent = HelloEvent(audioSocketAddress.hostname, audioSocketAddress.port, audioManager.secretKey.toList())
        send(helloEvent)
    }

    private fun clientReady(event: ReadyEvent) {
        audioSocketAddress = InetSocketAddress(event.ip, event.port).also {
            logger.info("${it.hostname}:${it.port} identified themselves")
        }
    }

    private fun closeConnection() {
        // TODO send close event
        session.close()
        //TODO gateway.connections.remove(this)
    }

}