package com.github.discordPodcasts.podcast.audioManager

import com.github.discordPodcasts.podcast.Podcast
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class AudioManager(
    selectorManager: SelectorManager,
    val coroutineScope: CoroutineScope,
    val podcast: Podcast
) {
    private val audioSocket = aSocket(selectorManager).udp().bind()
    val socketAddress = audioSocket.localAddress as InetSocketAddress
    val secretKey: ByteArray = Random.nextBytes(32)

    init {
        coroutineScope.launch {
            audioSocket.incoming.receiveAsFlow()
                //.filter { it.address == podcast.host?.address }
                .collect {

                    val host = podcast.host?.audioSocketAddress as SocketAddress? ?: return@collect
                    val sender = it.address
                    println(host)
                    println(sender)
                    if (host == sender) {
                        println("Host equals sender")
                    }
                    println(it.address)
                    println(audioSocket.localAddress)
                    println("received")
                    podcast.receivers
                        .mapNotNull { it.audioSocketAddress }
                        .map { address -> Datagram(it.packet, address) }
                        .forEach {
                            println("Sent")
                            audioSocket.outgoing.send(it)
                        }
                }
        }
    }
}