package org.nekrasov.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import org.nekrasov.exceptions.*
import org.nekrasov.exceptions.NotFoundException

fun Application.configureExceptions() {
    install(StatusPages){
        exception<Throwable> { call, throwable ->
            when(throwable) {
                is RequestValidationException -> {
                    call.respond(HttpStatusCode.BadRequest, mapOf("message" to throwable.reasons.first()))
                }
                is MissingHeaderException -> {
                    call.respond(HttpStatusCode.BadRequest, mapOf("message" to throwable.message))
                }
                is MissingQueryParameterException -> {
                    call.respond(HttpStatusCode.BadRequest, mapOf("message" to throwable.message))
                }
                is IncompatibleQueryParameterTypeException -> {
                    call.respond(HttpStatusCode.BadRequest, mapOf("message" to throwable.message))
                }
                is NotFoundException -> {
                    call.respond(HttpStatusCode.NotFound, mapOf("message" to throwable.message))
                }
                is UnauthorizedException -> {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("message" to throwable.message))
                }
                is ConflictException -> {
                    call.respond(HttpStatusCode.Conflict, mapOf("message" to throwable.message))
                }
                is ForbiddenException -> {
                    call.respond(HttpStatusCode.Forbidden, mapOf("message" to throwable.message))
                }
            }
        }
    }
}