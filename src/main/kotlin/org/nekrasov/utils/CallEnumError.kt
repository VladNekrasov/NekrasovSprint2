package org.nekrasov.utils

import io.ktor.server.application.*
import io.ktor.server.response.*

suspend inline fun ApplicationCall.respondErrorCode(errorCode: ErrorCode) {
    response.status(errorCode.code)
    respond(mapOf("message" to errorCode.message))
}