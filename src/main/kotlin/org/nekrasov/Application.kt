package org.nekrasov

import io.ktor.server.application.*
import org.nekrasov.data.DatabaseFactory
import org.nekrasov.data.repository.ChatRepository
import org.nekrasov.data.repository.UserRepository
import org.nekrasov.domain.service.AuthService
import org.nekrasov.domain.service.ChatService
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
    DatabaseFactory.init(environment.config)
    configureSerialization()
    configureAuth()
    configureWebSockets()
    configureRoutes(
        authService = authService,
        chatService = chatService
    )
}