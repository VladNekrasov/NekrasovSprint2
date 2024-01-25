package org.nekrasov

import io.ktor.server.application.*
import io.ktor.server.websocket.*
import org.nekrasov.data.DatabaseFactory
import org.nekrasov.data.repository.ChatRepository
import org.nekrasov.data.repository.UserRepository
import org.nekrasov.domain.service.AuthService
import org.nekrasov.domain.service.ChatService
import org.nekrasov.domain.service.UserService
import org.nekrasov.domain.service.WebSocketService
import org.nekrasov.plugins.configureAuth
import org.nekrasov.plugins.configureSerialization
import org.nekrasov.plugins.configureWebSockets
import org.nekrasov.plugins.configureRoutes

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val userRepository = UserRepository()
    val chatRepository = ChatRepository()
    val authService = AuthService(userRepository)
    val chatService = ChatService(chatRepository)
    val userService = UserService(userRepository)
    val webSocketService = WebSocketService()
    DatabaseFactory.init(environment.config)
    configureSerialization()
    configureAuth()
    configureWebSockets()
    configureRoutes(
        authService = authService,
        chatService = chatService,
        userService = userService,
        webSocketService = webSocketService
    )
}