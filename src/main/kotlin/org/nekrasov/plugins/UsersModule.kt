package org.nekrasov.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.nekrasov.domain.service.UserService
import org.nekrasov.routes.chatRoutes
import org.nekrasov.routes.userRoutes

fun Application.usersModule(userService: UserService) {
    routing {
        get("/") {
            call.respondText("Hello")
        }
        userRoutes(userService)
        chatRoutes()
    }
}