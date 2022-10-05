package com.github.discordPodcasts

import com.github.discordPodcasts.routes.createPodcast.createPodcast
import com.github.discordPodcasts.routes.getPodcast.getPodcast
import com.github.discordPodcasts.routes.listPodcasts.listPodcasts
import com.github.discordPodcasts.utility.WsError
import com.github.discordPodcasts.utility.closeWithError
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import loadAccountManager

fun main() {
    loadAccountManager()
    embeddedServer(Netty, port = 5050) {
        install(WebSockets)
        install(ContentNegotiation) { json() }

        routing {
            get("/podcast") { getPodcast() }
            post("/podcast") { createPodcast() }
            webSocket("/") {
                val podcastId = call.request.queryParameters["id"]

                if (podcastId !in Podcasts.active.keys) return@webSocket closeWithError(WsError.UNKNOWN_PODCAST)
                Podcasts.active[podcastId]?.sessions?.createSession(this)
            }
            get("/list") { listPodcasts() }
        }

    }.start(wait = true)
}

