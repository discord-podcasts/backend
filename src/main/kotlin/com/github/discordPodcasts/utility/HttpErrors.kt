package com.github.discordPodcasts.utility

import io.ktor.http.*

enum class HttpErrors(
    val statusCode: HttpStatusCode,
    val message: String
) {
    INVALID_BODY(HttpStatusCode.BadRequest, "Invalid body"),
    PARAM_MISSING(HttpStatusCode.BadRequest, "Query parameter missing"),
    NOT_FOUND(HttpStatusCode.NotFound, "Not found"),
    MISSING_AUTH(HttpStatusCode.Unauthorized, "Authentication data missing"),
    INVALID_AUTH(HttpStatusCode.Unauthorized, "Invalid authentication");
}