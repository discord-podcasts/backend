package com.github.discordPodcasts.routes.createPodcast


import com.github.discordPodcasts.Podcasts
import com.github.discordPodcasts.podcast.Podcast
import com.github.discordPodcasts.utility.Request
import com.github.discordPodcasts.utility.getAuthDataOrThrow
import com.github.discordPodcasts.utility.logInfo
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import java.net.BindException
import java.net.ServerSocket
import java.util.concurrent.ForkJoinPool

private val scope = CoroutineScope(ForkJoinPool.commonPool().asCoroutineDispatcher())

suspend fun Request.createPodcast() {
    val senderAuthentication = getAuthDataOrThrow() ?: return
    val potentialPodcast = CompletableDeferred<Podcast>()

    scope.launch {
        logInfo("create-podcasts") { "Creating new websocket server" }
        val podcast = Podcasts.createPotentialPodcast(senderAuthentication)
        potentialPodcast.complete(podcast)
    }

    call.respond(potentialPodcast.await())
}