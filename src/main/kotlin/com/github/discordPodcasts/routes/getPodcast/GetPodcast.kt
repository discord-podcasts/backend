package com.github.discordPodcasts.routes.getPodcast

import com.github.discordPodcasts.Podcasts
import com.github.discordPodcasts.utility.HttpErrors
import com.github.discordPodcasts.utility.Request
import com.github.discordPodcasts.utility.finishWithError
import com.github.discordPodcasts.utility.getAuthDataOrThrow
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.serialization.json.Json

suspend fun Request.getPodcast() {
    getAuthDataOrThrow() ?: return

    val podcastId = call.request.queryParameters["id"] ?: return finishWithError(HttpErrors.PARAM_MISSING)
    val podcast = Podcasts.active[podcastId] ?: return finishWithError(HttpErrors.NOT_FOUND)
    call.respond(podcast)
}