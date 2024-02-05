package org.nekrasov.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import org.nekrasov.domain.dto.request.*
import org.nekrasov.domain.dto.response.userToReadUserDto
import org.nekrasov.domain.service.AuthService
import org.nekrasov.domain.service.ChatService
import org.nekrasov.domain.service.WebSocketService
import org.nekrasov.exceptions.*

fun Route.chatRoutes(authService: AuthService,
                     chatService: ChatService,
                     webSocketService: WebSocketService){
    route("api/v1/chats") {
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

            val chatList = chatService.getAllChats(page, size)
            call.respond(HttpStatusCode.OK, chatList)
        }
        post{
            val token = call.request.headers["X-Auth-Token"] ?: throw MissingHeaderException("Missing  X-Auth-Token header")
            if (!authService.checkToken(token))
                throw UnauthorizedException("User not authorized")

            val createChatDto = call.receive<CreateChatDto>()
            if (!authService.checkUser(createChatDto.creatorId, token))
                throw ForbiddenException("The current user does not have access to this information")

            if (chatService.createChat(createChatDto))
                call.respond(HttpStatusCode.OK, mapOf("status" to "Ok"))
            else
                call.respond(HttpStatusCode.Conflict, mapOf("status" to "Duplicate user in chat"))
        }

        post("/join"){
            val token = call.request.headers["X-Auth-Token"] ?: throw MissingHeaderException("Missing  X-Auth-Token header")
            if (!authService.checkToken(token))
                throw UnauthorizedException("User not authorized")

            val chatDto = call.receive<ChatDto>()
            if (!authService.checkUser(chatDto.userId, token))
                throw ForbiddenException("The current user does not have access to this information")

            if (chatService.joinChat(chatDto)){
                call.respond(HttpStatusCode.OK, mapOf("status" to "Ok"))
            } else {
                call.respond(HttpStatusCode.BadRequest, mapOf("status" to "Duplicate user in chat"))
            }
        }

        post("/leave"){
            val token = call.request.headers["X-Auth-Token"] ?: throw MissingHeaderException("Missing  X-Auth-Token header")
            if (!authService.checkToken(token))
                throw UnauthorizedException("User not authorized")

            val chatDto = call.receive<ChatDto>()
            if (!authService.checkUser(chatDto.userId, token))
                throw ForbiddenException("The current user does not have access to this information")

            if (chatService.leaveChat(chatDto)){
                call.respond(HttpStatusCode.OK, mapOf("status" to "Ok"))
            } else {
                call.respond(HttpStatusCode.BadRequest, mapOf("status" to "Can't leave user from chat"))
            }
        }

        get("/{id}"){
            val token = call.request.headers["X-Auth-Token"] ?: throw MissingHeaderException("Missing  X-Auth-Token header")
            if (!authService.checkToken(token))
                throw UnauthorizedException("User not authorized")
            val id = call.parameters["id"] ?: throw MissingQueryParameterException("Parameter id not specified in query")
            val idChat = id.toLongOrNull() ?: throw IncompatibleQueryParameterTypeException("Parameter id requires the Long type")

            chatService.getChat(idChat)?.let{
                call.respond(HttpStatusCode.Found, it)
            } ?: call.respond(HttpStatusCode.NotFound, mapOf("status" to "Chat not found"))
        }

        get("/participants"){
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
                call.respond(HttpStatusCode.OK, chatService.getChatParticipants(chatDto.chatId, page, size).map(::userToReadUserDto))
            } ?: call.respond(HttpStatusCode.NotFound, mapOf("status" to "Chat not found"))
        }

        patch{
            val token = call.request.headers["X-Auth-Token"] ?: throw MissingHeaderException("Missing  X-Auth-Token header")
            if (!authService.checkToken(token))
                throw UnauthorizedException("User not authorized")

            val updateChatDto = call.receive<UpdateChatDto>()

            if (chatService.updateChat(updateChatDto)){
                call.respond(HttpStatusCode.OK, mapOf("status" to "Ok"))
            } else {
                call.respond(HttpStatusCode.BadRequest, mapOf("status" to "Refusal to edit chat"))
            }
        }

        delete {
            val token = call.request.headers["X-Auth-Token"] ?: throw MissingHeaderException("Missing  X-Auth-Token header")
            if (!authService.checkToken(token))
                throw UnauthorizedException("User not authorized")
            val chatDto = call.receive<ChatDto>()

            if (chatService.deleteChat(chatDto)){
                call.respond(HttpStatusCode.OK, mapOf("status" to "Ok"))
            } else {
                call.respond(HttpStatusCode.BadRequest, mapOf("status" to "Refusal to delete chat"))
            }
        }
    }

    webSocket("/chat-socket"){
        val token = call.request.headers["X-Auth-Token"]
        if (token == null){
            close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, "Missing  X-Auth-Token header"))
            return@webSocket
        }

        if (!authService.checkToken(token)){
            close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, "User not authorized"))
            return@webSocket
        }

        val idU = call.parameters["idUser"] ?: run{
            close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, "Parameter idUser not specified in query"))
            return@webSocket
        }

        val idUser = idU.toLongOrNull() ?: run{
            close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, "Parameter idUser requires the Long type"))
            return@webSocket
        }

        val idC = call.parameters["idChat"] ?: run{
            close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, "Parameter idChat not specified in query"))
            return@webSocket
        }

        val idChat = idC.toLongOrNull() ?: run{
            close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, "Parameter idChat requires the Long type"))
            return@webSocket
        }

        val pageParameter = call.parameters["page"] ?: throw MissingQueryParameterException("Parameter page not specified in query")
        val page = pageParameter.toLongOrNull() ?: throw IncompatibleQueryParameterTypeException("Parameter page requires the Long type")
        val sizeParameter = call.parameters["size"] ?: throw MissingQueryParameterException("Parameter size not specified in query")
        val size = sizeParameter.toIntOrNull() ?: throw IncompatibleQueryParameterTypeException("Parameter size requires the Int type")
        if (size<=0 || page<=0){
            close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, "Parameter size and page more than zero"))
            return@webSocket
        }

        if (!authService.checkUser(idUser, token)){
            close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, "The current user does not have access to this information"))
            return@webSocket
        }

        if (!chatService.checkParticipant(idChat, idUser)){
            close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, "The current user is not a participant"))
            return@webSocket
        }

        try {
            webSocketService.onConnect(idChat, page, size, this)
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val receivedText = frame.readText()
                webSocketService.onMessage(receivedText, idChat, idUser, this)
            }
        } catch (e: Exception) {
            println(e.localizedMessage)
            webSocketService.onClose(idChat, this)
        } finally {
            webSocketService.onClose(idChat, this)
        }
    }
}