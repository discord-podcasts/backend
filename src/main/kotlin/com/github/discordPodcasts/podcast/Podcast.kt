package com.github.discordPodcasts.podcast

import com.github.discordPodcasts.Podcasts
import com.github.discordPodcasts.utility.Authentication
import com.github.discordPodcasts.utility.WsError
import com.github.discordPodcasts.utility.logInfo
import com.github.discordPodcasts.utility.logWarning
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.Serializable
import java.util.concurrent.ForkJoinPool
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

@Serializable(with = PodcastSerializer::class)
data class Podcast(
    var id: String,
    val senderAuthentication: Authentication,
    var activeSince: Long? = null,
    val coroutineScope: CoroutineScope = CoroutineScope(ForkJoinPool.commonPool().asCoroutineDispatcher())
) {
    val secretKey: ByteArray = Random.nextBytes(32)
    val sessions = PodcastSessionManager(this)
    val pending: Boolean get() = activeSince == null

    init {
        verifyConnection()
    }

    /**
     * Stops the web server and removes the podcast from [Podcasts.active].
     */
    suspend fun destroy(reason: WsError) {
        Podcasts.active.remove(id)
        sessions.disconnectAll(reason)
        logInfo("podcast-$id") { "Closed podcast" }
    }

    /**
     * Verifies the websocket connection by making sure that the sender connects in a given time.
     */
    private fun verifyConnection() = coroutineScope.launch {
        val connectedSender = withTimeoutOrNull(10.seconds) { sessions.sender.await() }
        // Sender didn't connect in time
        if (connectedSender == null) {
            destroy(WsError.SENDER_TIMEOUT)
            logWarning("podcast-${id}") { "Initial connection timeout of podcast ${id}, shutting down..." }
        }
    }

}