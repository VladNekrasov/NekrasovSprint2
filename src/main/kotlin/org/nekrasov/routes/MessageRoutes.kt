package org.nekrasov.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.nekrasov.domain.dto.request.ChatDto
import org.nekrasov.domain.service.AuthService
import org.nekrasov.domain.service.ChatService
import org.nekrasov.domain.service.MessageService
import org.nekrasov.exceptions.*

fun Route.messageRoutes(messageService: MessageService,
                        authService: AuthService,
                        chatService: ChatService){
    route("/api/v1/messages") {
        get{
            val token = call.request.headers["X-Auth-Token"] ?: throw MissingHeaderException("Missing  X-Auth-Token header")
            if (!authService.checkToken(token))
                throw UnauthorizedException("User not authorized")
            val pageParameter = call.parameters["page"] ?: throw MissingQueryParameterException("Parameter page not specified in query")
            val page = pageParameter.toLongOrNull() ?: throw IncompatibleQueryParameterTypeException("Parameter page requires the Long type")
            val sizeParameter = call.parameters["size"] ?: throw MissingQueryParameterException("Parameter size not specified in query")
            val size = sizeParameter.toIntOrNull() ?: throw IncompatibleQueryParameterTypeException("Parameter size requires the Int type")
            if (size<=0 || page<=0)
                throw IncompatibleQueryParameterTypeException("Parameter size and page more than zero")

            val chatDto = call.receive<ChatDto>()
            if (!authService.checkUser(chatDto.userId, token))
                throw ForbiddenException("The current user does not have access to this information")
            if (!chatService.checkParticipant(chatDto.chatId, chatDto.userId))
                throw ForbiddenException("The current user is not a participant")

            chatService.getChat(chatDto.chatId)?.let {
                call.respond(HttpStatusCode.OK, messageService.getAllMessages(page, size, chatDto.chatId))
            } ?: call.respond(HttpStatusCode.NotFound, mapOf("status" to "Chat not found"))
        }
    }
}