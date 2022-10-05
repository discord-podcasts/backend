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
        element("port", PrimitiveSerialDescriptor("port", PrimitiveKind.INT))
        element("ip", PrimitiveSerialDescriptor("ip", PrimitiveKind.STRING))
        element("activeSince", PrimitiveSerialDescriptor("activeSince", PrimitiveKind.LONG))
    }

    override fun deserialize(decoder: Decoder): Podcast {
        TODO("Not yet implemented")
    }

    override fun serialize(encoder: Encoder, value: Podcast) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.id)
            val connector = value.server.environment.connectors.first()
            encodeIntElement(descriptor, 1, connector.port)
            encodeStringElement(descriptor, 2, connector.host)
            encodeNullableSerializableElement(descriptor, 3, Long.serializer(), value.activeSince)
        }
    }

}