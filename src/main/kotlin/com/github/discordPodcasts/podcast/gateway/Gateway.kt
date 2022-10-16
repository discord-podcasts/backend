package com.github.discordPodcasts.podcast.gateway

import com.github.discordPodcasts.podcast.audioManager.AudioManager
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class Gateway(
    selectorManager: SelectorManager,
    val audioManager: AudioManager,
    private val coroutineScope: CoroutineScope
) {
    private val socket = aSocket(selectorManager).tcp().bind()
    val socketAddress = socket.localAddress as InetSocketAddress
    val connections = mutableListOf<GatewaySession>()

    init {
        start()
    }

    private fun start() = coroutineScope.launch {
        while (true) {
            val session = GatewaySession(socket.accept(), audioManager, coroutineScope)
            connections.add(session)
        }
    }

}