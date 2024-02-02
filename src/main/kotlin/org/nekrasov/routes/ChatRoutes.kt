package org.nekrasov.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.json.Json
import org.nekrasov.domain.dto.request.*
import org.nekrasov.domain.dto.response.ResponseMessageDto
import org.nekrasov.domain.dto.response.userToReadUserDto
import org.nekrasov.domain.service.AuthService
import org.nekrasov.domain.service.ChatService
import org.nekrasov.domain.service.WebSocketService
import org.nekrasov.exceptions.IncompatibleQueryParameterTypeException
import org.nekrasov.exceptions.MissingHeaderException
import org.nekrasov.exceptions.MissingQueryParameterException
import org.nekrasov.exceptions.UnauthorizedException

fun Route.chatRoutes(authService: AuthService,
                     chatService: ChatService,
                     webSocketService: WebSocketService){
    route("/chats") {
        post{
            val token = call.request.headers["X-Auth-Token"] ?: throw MissingHeaderException("Missing  X-Auth-Token header")
            if (!authService.checkToken(token))
                throw UnauthorizedException("User not authorized")

            val createChatDto = call.receive<CreateChatDto>()
            if (chatService.createChat(createChatDto))
                call.respond(HttpStatusCode.OK, mapOf("status" to "Ok"))
            else
                call.respond(HttpStatusCode.Conflict, mapOf("status" to "Duplicate user in chat"))
        }

        post("/join"){
            val token = call.request.headers["X-Auth-Token"] ?: throw MissingHeaderException("Missing  X-Auth-Token header")
            if (!authService.checkToken(token))
                throw UnauthorizedException("User not authorized")

            val joinLeaveChatDto = call.receive<JoinLeaveChatDto>()

            if (chatService.joinChat(joinLeaveChatDto)){
                call.respond(HttpStatusCode.OK, mapOf("status" to "Ok"))
            } else {
                call.respond(HttpStatusCode.BadRequest, mapOf("status" to "Duplicate user in chat"))
            }
        }

        post("/leave"){
            val token = call.request.headers["X-Auth-Token"] ?: throw MissingHeaderException("Missing  X-Auth-Token header")
            if (!authService.checkToken(token))
                throw UnauthorizedException("User not authorized")

            val joinLeaveChatDto = call.receive<JoinLeaveChatDto>()

            if (chatService.leaveChat(joinLeaveChatDto)){
                call.respond(HttpStatusCode.OK, mapOf("status" to "Ok"))
            } else {
                call.respond(HttpStatusCode.BadRequest, mapOf("status" to "Can't leave user from chat"))
            }
        }

        get{
            val token = call.request.headers["X-Auth-Token"] ?: throw MissingHeaderException("Missing  X-Auth-Token header")
            if (!authService.checkToken(token))
                throw UnauthorizedException("User not authorized")
            val id = call.parameters["id"] ?: throw MissingQueryParameterException("Parameter id not specified in query")
            val idChat = id.toLongOrNull() ?: throw IncompatibleQueryParameterTypeException("Parameter id requires the Long type")

            chatService.getChat(idChat)?.let{
                call.respond(HttpStatusCode.Found, it)
            } ?: call.respond(HttpStatusCode.NotFound, mapOf("status" to "Chat not found"))
        }

        get("participants"){
            val token = call.request.headers["X-Auth-Token"] ?: throw MissingHeaderException("Missing  X-Auth-Token header")
            if (!authService.checkToken(token))
                throw UnauthorizedException("User not authorized")
            val id = call.parameters["id"] ?: throw MissingQueryParameterException("Parameter id not specified in query")
            val idChat = id.toLongOrNull() ?: throw IncompatibleQueryParameterTypeException("Parameter id requires the Long type")

            call.respond(HttpStatusCode.OK, chatService.getChatParticipants(idChat).map(::userToReadUserDto))
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
            val deleteChatDto = call.receive<DeleteChatDto>()

            if (chatService.deleteChat(deleteChatDto)){
                call.respond(HttpStatusCode.OK, mapOf("status" to "Ok"))
            } else {
                call.respond(HttpStatusCode.BadRequest, mapOf("status" to "Refusal to delete chat"))
            }
        }

        get("/all"){
            val token = call.request.headers["X-Auth-Token"] ?: throw MissingHeaderException("Missing  X-Auth-Token header")
            if (!authService.checkToken(token))
                throw UnauthorizedException("User not authorized")

            val chatList = chatService.getAllChats()
            call.respond(HttpStatusCode.OK, chatList)
        }
    }

    webSocket("/chat"){
        try {
            //val room = webSocketService.onConnect(idChat, this)
            incoming.consumeEach {
                frame ->
                if (frame is Frame.Text) {
                    val sendMessageDto = Json.decodeFromString<ResponseMessageDto>(frame.readText())
                    send(Frame.Text(sendMessageDto.toString()))
                }
            }
        } catch (e: Exception) {
            println(e.localizedMessage)
            //
        } finally {
            //
        }
//        val id = call.parameters["id"] ?: run{
//            close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, "Parameter id not specified in query"))
//            return@webSocket
//        }
//
//        val idChat = id.toLongOrNull() ?: run{
//            close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, "Parameter id requires the Long type"))
//            return@webSocket
//        }
//
//
//        chatService.getChat(idChat) ?: run{
//            close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, "Chat does not exist"))
//            return@webSocket
//        }
//
//        val token = call.request.headers["X-Auth-Token"]
//        if (!authService.checkToken(token)){
//            close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, "User not authorized"))
//            return@webSocket
//        }
//
//        try {
//            val room = webSocketService.onConnect(idChat, this)
//            incoming.consumeEach {
//                frame ->
//                if (frame is Frame.Text) {
//                    webSocketService.onMessage(frame.readText(), room, token!!)
//                }
//            }
//        } catch (e: Exception) {
//            webSocketService.onClose(idChat, this)
//            println(e.localizedMessage)
//        } finally {
//            webSocketService.onClose(idChat, this)
//        }
    }
}