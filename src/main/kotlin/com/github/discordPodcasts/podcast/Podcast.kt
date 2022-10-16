package com.github.discordPodcasts.podcast

import com.github.discordPodcasts.podcast.audioManager.AudioManager
import com.github.discordPodcasts.podcast.gateway.Gateway
import com.github.discordPodcasts.utility.Authentication
import io.ktor.network.selector.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.serialization.Serializable
import java.util.concurrent.ForkJoinPool

@Serializable(with = PodcastSerializer::class)
data class Podcast(
    var id: String,
    val hostAuth: Authentication,
    var activeSince: Long? = null
) {
    private val coroutineScope: CoroutineScope = CoroutineScope(ForkJoinPool.commonPool().asCoroutineDispatcher())

    private val selectorManager = SelectorManager(Dispatchers.IO)
    val audioManager = AudioManager(selectorManager, coroutineScope, this)
    val gateway = Gateway(selectorManager, audioManager, coroutineScope)

    val host get() = gateway.connections.find { it.claimsHost && hostAuth == it.authentication }
    val receivers get() = gateway.connections.filter { !it.claimsHost && it.authentication != null }
}