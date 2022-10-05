package com.github.discordPodcasts

enum class PacketType(val raw: Byte) {
    EVENT(0.toByte()),
    AUDIO(1.toByte());
}