package com.github

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.network.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive

suspend fun main() {
    val httpClient = HttpClient(CIO)
    println(0)
    val json = httpClient.post("http://127.0.0.1:5050/podcast") {
        header("clientId", "5341622813433566")
        header("token", "zbwQOcIZsiJRCTh56rYnqqnALT")
    }.bodyAsText()
    val podcast = Json.decodeFromString<JsonObject>(json)
    val port = podcast["port"]?.jsonPrimitive?.int ?: throw Exception("No port")

    println("Connecting to 127.0.0.1:$port")
    val selectorManager = SelectorManager(Dispatchers.IO)
    val socket = aSocket(selectorManager).udp().connect(InetSocketAddress("127.0.0.1", port))

    val myPort = socket.localAddress.toJavaAddress().port
    val bytes = BytePacketBuilder().apply {
        writeInt(myPort)
    }.build()
    val datagram = Datagram(bytes, InetSocketAddress("127.0.0.1", port))
    socket.send(datagram)

}