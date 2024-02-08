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
import org.nekrasov.domain.service.UserService
import org.nekrasov.domain.service.WebSocketService
import org.nekrasov.exceptions.*
import org.nekrasov.utils.ServiceResult
import org.nekrasov.utils.getQueryParameter
import org.nekrasov.utils.respondErrorCode

fun Route.chatRoutes(authService: AuthService,
                     userService: UserService,
                     chatService: ChatService,
                     webSocketService: WebSocketService){
    route("api/v1/chats") {

        get{
            val token = call.request.headers["X-Auth-Token"] ?: throw MissingHeaderException("Missing  X-Auth-Token header")
            if (!authService.checkToken(token))
                throw UnauthorizedException("User not authorized")

            val page = call.getQueryParameter<Long>("page")
            val size = call.getQueryParameter<Int>("size")

            chatService.getAllChats(page, size)?.let{
                call.respond(HttpStatusCode.OK, it)
            } ?: call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Parameter size and page more than zero"))
        }

        post{
            val token = call.request.headers["X-Auth-Token"] ?: throw MissingHeaderException("Missing  X-Auth-Token header")
            if (!authService.checkToken(token))
                throw UnauthorizedException("User not authorized")

            val createChatDto = call.receive<CreateChatDto>()
            if (!authService.checkUser(createChatDto.creatorId, token))
                throw ForbiddenException("The current user does not have access to create chat")

            when(val result = chatService.createChat(createChatDto)){
                is ServiceResult.Success -> {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Ok"))
                }
                is ServiceResult.Error -> {
                    call.respondErrorCode(result.error)
                }
            }
        }

        post("/join"){
            val token = call.request.headers["X-Auth-Token"] ?: throw MissingHeaderException("Missing  X-Auth-Token header")
            if (!authService.checkToken(token))
                throw UnauthorizedException("User not authorized")

            val chatDto = call.receive<ChatDto>()
            if (!authService.checkUser(chatDto.userId, token))
                throw ForbiddenException("The current user does not have access to join operation")

            when(val result = chatService.joinChat(chatDto)){
                is ServiceResult.Success -> {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Ok"))
                }
                is ServiceResult.Error -> {
                    call.respondErrorCode(result.error)
                }
            }
        }

        post("/leave"){
            val token = call.request.headers["X-Auth-Token"] ?: throw MissingHeaderException("Missing  X-Auth-Token header")
            if (!authService.checkToken(token))
                throw UnauthorizedException("User not authorized")

            val chatDto = call.receive<ChatDto>()
            if (!authService.checkUser(chatDto.userId, token))
                throw ForbiddenException("The current user does not have access to this information")

            when(val result = chatService.leaveChat(chatDto)){
                is ServiceResult.Success -> {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Ok"))
                }
                is ServiceResult.Error -> {
                    call.respondErrorCode(result.error)
                }
            }
        }

        get("/{id}"){
            val token = call.request.headers["X-Auth-Token"] ?: throw MissingHeaderException("Missing  X-Auth-Token header")
            if (!authService.checkToken(token))
                throw UnauthorizedException("User not authorized")
            val id = call.getQueryParameter<Long>("id")

            chatService.getChat(id)?.let{
                call.respond(HttpStatusCode.Found, it)
            } ?: call.respond(HttpStatusCode.NotFound, mapOf("status" to "Chat not found"))
        }

        get("/participants"){
            val token = call.request.headers["X-Auth-Token"] ?: throw MissingHeaderException("Missing  X-Auth-Token header")
            if (!authService.checkToken(token))
                throw UnauthorizedException("User not authorized")

            val page = call.getQueryParameter<Long>("page")
            val size = call.getQueryParameter<Int>("size")

            val chatDto = call.receive<ChatDto>()
            if (!authService.checkUser(chatDto.userId, token))
                throw ForbiddenException("The current user does not have access to this information")

            when(val result = chatService.getChatParticipants(chatDto, page, size)){
                is ServiceResult.Success -> {
                    call.respond(HttpStatusCode.OK, result.data.map(::userToReadUserDto))
                }
                is ServiceResult.Error -> {
                    call.respondErrorCode(result.error)
                }
            }
        }

        patch{
            val token = call.request.headers["X-Auth-Token"] ?: throw MissingHeaderException("Missing  X-Auth-Token header")
            if (!authService.checkToken(token))
                throw UnauthorizedException("User not authorized")

            val updateChatDto = call.receive<UpdateChatDto>()

            when(val result = chatService.updateChat(updateChatDto)){
                is ServiceResult.Success -> {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Ok"))
                }
                is ServiceResult.Error -> {
                    call.respondErrorCode(result.error)
                }
            }
        }

        delete {
            val token = call.request.headers["X-Auth-Token"] ?: throw MissingHeaderException("Missing  X-Auth-Token header")
            if (!authService.checkToken(token))
                throw UnauthorizedException("User not authorized")

            val chatDto = call.receive<ChatDto>()

            when(val result = chatService.deleteChat(chatDto)){
                is ServiceResult.Success -> {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Ok"))
                }
                is ServiceResult.Error -> {
                    call.respondErrorCode(result.error)
                }
            }
        }
    }

    webSocket("/chat-socket"){
//        val token = call.request.headers["X-Auth-Token"]
//        if (token == null){
//            close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, "Missing  X-Auth-Token header"))
//            return@webSocket
//        }
//
//        if (!authService.checkToken(token)){
//            close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, "User not authorized"))
//            return@webSocket
//        }

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

        val sizeParameter = call.parameters["size"] ?: run{
            close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, "Parameter size not specified in query"))
            return@webSocket
        }

        val size = sizeParameter.toIntOrNull() ?: run{
            close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, "Parameter size requires the Int type"))
            return@webSocket
        }

        if (size<=0){
            close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, "Parameter size more than zero"))
            return@webSocket
        }


//        if (!authService.checkUser(idUser, token)){
//            close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, "The current user does not have access to this information"))
//            return@webSocket
//        }

        if (userService.getUser(idUser) == null){
            close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, "User not found"))
            return@webSocket
        }

        if (chatService.getChat(idChat) == null){
            close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, "Chat not found"))
            return@webSocket
        }

        if (!chatService.checkParticipant(idChat, idUser)){
            close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, "The current user is not a participant"))
            return@webSocket
        }

        try {
            webSocketService.onConnect(idChat, size, this)
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