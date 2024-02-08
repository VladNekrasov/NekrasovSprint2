package org.nekrasov.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.nekrasov.domain.dto.request.ChatDto
import org.nekrasov.domain.dto.response.userToReadUserDto
import org.nekrasov.domain.service.AuthService
import org.nekrasov.domain.service.ChatService
import org.nekrasov.domain.service.MessageService
import org.nekrasov.exceptions.*
import org.nekrasov.utils.getQueryParameter

fun Route.messageRoutes(messageService: MessageService,
                        authService: AuthService,
                        chatService: ChatService){
    route("/api/v1/messages") {
        get{
            val token = call.request.headers["X-Auth-Token"] ?: throw MissingHeaderException("Missing  X-Auth-Token header")
            if (!authService.checkToken(token))
                throw UnauthorizedException("User not authorized")

            val page = call.getQueryParameter<Long>("page")
            val size = call.getQueryParameter<Int>("size")

            val chatDto = call.receive<ChatDto>()
            if (!authService.checkUser(chatDto.userId, token))
                throw ForbiddenException("The current user does not have access to this information")
            if (!chatService.checkParticipant(chatDto.chatId, chatDto.userId))
                throw ForbiddenException("The current user is not a participant")

            messageService.getAllMessages(page, size, chatDto.chatId)?.let{
                call.respond(HttpStatusCode.OK, it)
            } ?: call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Parameter size and page more than zero"))
        }
    }
}