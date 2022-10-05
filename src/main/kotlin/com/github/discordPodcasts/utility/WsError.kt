package com.github.discordPodcasts.utility

import io.ktor.websocket.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = WsError.Serializer::class)
enum class WsError(
    val code: Short,
    val message: String
) {
    MISSING_AUTH(4000, "Authentication data missing"),
    INVALID_AUTH(4001, "Invalid authentication"),
    INVALID_PAYLOAD(4002, "The sender send invalid payload"),
    SENDER_DISCONNECT(4003, "Sender disconnected"),
    SENDER_ALREADY_CONNECTED(4004, "A sender is already connected to this podcast"),
    SENDER_TIMEOUT(4005, "No sender connected during the time limit"),
    UNKNOWN_PODCAST(4006, "No podcast found");

    val asCloseReason get():CloseReason = CloseReason(code, message)

    internal object Serializer : KSerializer<WsError> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("websocket_errors", PrimitiveKind.SHORT)
        override fun deserialize(decoder: Decoder): WsError = decoder.decodeShort().let { code -> values().first { it.code == code } }
        override fun serialize(encoder: Encoder, value: WsError) = encoder.encodeShort(value.code)
    }

}