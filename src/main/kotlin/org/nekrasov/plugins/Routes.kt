package org.nekrasov.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.nekrasov.domain.service.AuthService
import org.nekrasov.domain.service.ChatService
import org.nekrasov.domain.service.UserService
import org.nekrasov.domain.service.WebSocketService
import org.nekrasov.routes.authRoutes
import org.nekrasov.routes.chatRoutes
import org.nekrasov.routes.userRoutes

fun Application.configureRoutes(authService: AuthService, chatService: ChatService, userService: UserService, webSocketService: WebSocketService) {
    routing {
        get("/") {
            call.respondText("Hello")
        }
        authRoutes(authService)
        chatRoutes(authService, chatService, webSocketService)
        userRoutes(userService)
    }
}