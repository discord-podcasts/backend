package com.github.discordPodcasts.podcast.events

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.text.toByteArray

val jsonConfiguration = Json {
    encodeDefaults = true
    explicitNulls = true
}

inline fun <reified T> encodeToBytes(value: T): ByteArray {
    val string = jsonConfiguration.encodeToString(value)
    return string.toByteArray()
}