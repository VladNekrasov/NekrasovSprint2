package org.nekrasov.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.nekrasov.domain.dto.request.CreateChatDto
import org.nekrasov.domain.service.AuthService
import org.nekrasov.domain.service.ChatService

fun Route.chatRoutes(authService: AuthService, chatService: ChatService){
    route("/chats") {
        post{
            val createChatDto = call.receive<CreateChatDto>()
            val token: String? = call.request.headers["X-Auth-Token"]
            if (token != null && authService.checkToken(token)){
                chatService.createChat(createChatDto)
                call.respond(HttpStatusCode.OK, mapOf("status" to "Ok"))
            }
            else
                call.respond(HttpStatusCode.Unauthorized, mapOf("status" to "User not authorized"))
        }
    }
}