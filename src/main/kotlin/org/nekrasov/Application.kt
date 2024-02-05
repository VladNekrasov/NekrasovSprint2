package org.nekrasov

import io.ktor.server.application.*
import org.nekrasov.data.DatabaseFactory
import org.nekrasov.data.repository.ChatRepository
import org.nekrasov.data.repository.MessageRepository
import org.nekrasov.data.repository.UserChatRepository
import org.nekrasov.data.repository.UserRepository
import org.nekrasov.domain.service.*
import org.nekrasov.plugins.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val userRepository = UserRepository()
    val chatRepository = ChatRepository()
    val userChatRepository = UserChatRepository()
    val messageRepository = MessageRepository()
    val authService = AuthService(userRepository, chatRepository)
    val chatService = ChatService(chatRepository, userRepository, userChatRepository)
    val userService = UserService(userRepository, chatRepository, userChatRepository)
    val webSocketService = WebSocketService(messageRepository)
    val messageService = MessageService(messageRepository)
    DatabaseFactory.init(environment.config)
    configureRequestValidation()
    configureSerialization()
    configureAuth()
    configureWebSockets()
    configureRoutes(
        authService = authService,
        chatService = chatService,
        userService = userService,
        webSocketService = webSocketService,
        messageService = messageService
    )
    configureExceptions()
}