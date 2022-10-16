package com.github.discordPodcasts.routes.listPodcasts

import com.github.discordPodcasts.Podcasts
import com.github.discordPodcasts.utility.Request
import com.github.discordPodcasts.utility.getAuthDataOrThrow
import io.ktor.server.application.*
import io.ktor.server.response.*

suspend fun Request.listPodcasts() {
    getAuthDataOrThrow() ?: return
    call.respond(Podcasts.active.values)
}