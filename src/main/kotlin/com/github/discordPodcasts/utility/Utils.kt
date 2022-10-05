package com.github.discordPodcasts.utility

import accounts
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.websocket.*
import io.ktor.util.pipeline.*
import io.ktor.websocket.*
import kotlinx.coroutines.CompletableDeferred
import org.slf4j.LoggerFactory

typealias Request = PipelineContext<Unit, ApplicationCall>

suspend fun Request.finishWithError(error: HttpErrors) = call.respond(error.statusCode, error.message)
suspend fun DefaultWebSocketServerSession.closeWithError(error: WsError) = close(CloseReason(error.code, error.message))
suspend fun <T> CompletableDeferred<T>.getOrNull(): T? = if (isCompleted) await() else null

suspend inline fun <reified T> ApplicationCall.tryReceive(): T? {
    return try {
        receive()
    } catch (e: ContentTransformationException) {
        null
    }
}

suspend fun Request.getAuthDataOrThrow(): Authentication? {
    val clientId = call.request.header("clientId")
    val token = call.request.header("token")
    if (clientId == null || token == null) {
        finishWithError(HttpErrors.MISSING_AUTH)
        return null
    }

    // Invalid authentication
    if (clientId !in accounts.keys || accounts[clientId] != token) {
        finishWithError(HttpErrors.INVALID_AUTH)
        return null
    }

    return Authentication(clientId, token)
}

suspend fun DefaultWebSocketServerSession.getAuthDataOrThrow(): Authentication? {
    val clientId = call.request.header("clientId")
    val token = call.request.header("token")
    if (clientId == null || token == null) {
        closeWithError(WsError.MISSING_AUTH)
        return null
    }

    // Invalid authentication
    if (clientId !in accounts.keys || accounts[clientId] != token) {
        closeWithError(WsError.INVALID_AUTH)
        return null
    }

    return Authentication(clientId, token)
}

fun logInfo(name: String, message: Unit.() -> String) = LoggerFactory.getLogger(name).info(message.invoke(Unit))
fun logWarning(name: String, message: Unit.() -> String) = LoggerFactory.getLogger(name).warn(message.invoke(Unit))