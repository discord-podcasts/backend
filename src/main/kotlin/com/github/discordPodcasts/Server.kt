package com.github.discordPodcasts

import com.github.discordPodcasts.routes.createPodcast.createPodcast
import com.github.discordPodcasts.routes.getPodcast.getPodcast
import com.github.discordPodcasts.routes.listPodcasts.listPodcasts
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import loadAccountManager

fun main() {
    loadAccountManager()
    embeddedServer(Netty, port = 5050) {
        install(WebSockets)
        install(ContentNegotiation) { json() }

        routing {
            get("/ip") {
                var ip = call.request.origin.host
                if (call.request.origin.host == "127.0.0.1") ip = "91.21.69.151"
                call.respond(ip)
            }
            get("/podcast") { getPodcast() }
            post("/podcast") { createPodcast() }
            get("/list") { listPodcasts() }
        }

    }.start(wait = true)
}

