package com.github.discordPodcasts

import com.github.discordPodcasts.podcast.Podcast
import com.github.discordPodcasts.utility.Authentication
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object Podcasts {

    val active = mutableMapOf<String, Podcast>()

    suspend fun createPotentialPodcast(senderAuthentication: Authentication): Podcast {
        var id = generateId()
        while (active.containsKey(id)) id = generateId()

        val podcast = Podcast(id, senderAuthentication)
        active[id] = podcast
        return podcast
    }

    private fun generateId(): String {
        val characters = ('A'..'Z') + ('a'..'z') + ('0'..'9') + "-_"
        return (1..5).map { characters.random() }.joinToString("")
    }

}