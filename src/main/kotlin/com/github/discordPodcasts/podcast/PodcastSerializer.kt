package com.github.discordPodcasts.podcast

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure

object PodcastSerializer : KSerializer<Podcast> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("podcast") {
        element("id", PrimitiveSerialDescriptor("id", PrimitiveKind.STRING))
        element("host", PrimitiveSerialDescriptor("host", PrimitiveKind.STRING))
        element("ip", PrimitiveSerialDescriptor("ip", PrimitiveKind.STRING))
        element("port", PrimitiveSerialDescriptor("port", PrimitiveKind.INT))
        element("activeSince", PrimitiveSerialDescriptor("activeSince", PrimitiveKind.LONG))
    }

    override fun deserialize(decoder: Decoder): Podcast {
        TODO("Not yet implemented")
    }

    override fun serialize(encoder: Encoder, value: Podcast) {
        encoder.encodeStructure(descriptor) {
            var i = 0
            encodeStringElement(descriptor, i++, value.id)
            encodeStringElement(descriptor, i++, value.hostAuth.id)
            encodeStringElement(descriptor, i++, value.gateway.socketAddress.hostname)
            encodeIntElement(descriptor, i++, value.gateway.socketAddress.port)
            encodeNullableSerializableElement(descriptor, i++, Long.serializer(), value.activeSince)
        }
    }

}