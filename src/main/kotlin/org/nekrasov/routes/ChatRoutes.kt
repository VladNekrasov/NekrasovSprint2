package org.nekrasov.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import org.nekrasov.domain.dto.request.CreateChatDto
import org.nekrasov.domain.service.AuthService
import org.nekrasov.domain.service.ChatService
import org.nekrasov.domain.service.WebSocketService

fun Route.chatRoutes(authService: AuthService, chatService: ChatService, webSocketService: WebSocketService){
    route("/chats") {
        post{
            val createChatDto = call.receive<CreateChatDto>()
            val token: String? = call.request.headers["X-Auth-Token"]
            if (authService.checkToken(token)){
                chatService.createChat(createChatDto)
                call.respond(HttpStatusCode.OK, mapOf("status" to "Ok"))
            }
            else
                call.respond(HttpStatusCode.Unauthorized, mapOf("status" to "User not authorized"))
        }
    }

    webSocket("/chat"){
        val id = call.parameters["id"] ?: run{
            close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, "Parameter id not specified in query"))
            return@webSocket
        }

        val idChat = id.toLongOrNull() ?: run{
            close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, "Parameter id requires the Long type"))
            return@webSocket
        }

        chatService.getChat(idChat) ?: run{
            close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, "Chat does not exist"))
            return@webSocket
        }

        val token = call.request.headers["X-Auth-Token"]
        if (!authService.checkToken(token)){
            close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, "User not authorized"))
            return@webSocket
        }

        try {
            val room = webSocketService.onConnect(idChat, this)
            incoming.consumeEach { frame ->
                if (frame is Frame.Text) {
                    webSocketService.onMessage(frame.readText(), room)
                }
            }
        } catch (e: Exception) {
            webSocketService.onClose(idChat, this)
            println(e.localizedMessage)
        } finally {
            webSocketService.onClose(idChat, this)
        }
















//        val id = call.parameters["id"]?.let {
//            val token: String? = call.request.headers["X-Auth-Token"]
//
//            val idChat = it.toLongOrNull()?.let { it2->
//                val chat = chatService.getChat(it2)
//            }
//            close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, "Invalid parameter for id"))
//
//        } ?: close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, "Parameter id not specified in query"))














//        val id: Long? = call.request.queryParameters["id"]?.toLongOrNull()
//        if (id == null){
//            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Invalid parameter for id"))
//            return@webSocket
//        }
//
//        if (connections[id] == null)
//            connections[id] = mutableListOf<DefaultWebSocketSession>(this)
//        else
//            connections[id]!!.add(this)
//
//        try {
//
//            for (frame in incoming) {
//                frame as? Frame.Text ?: continue
//                val message = frame.readText()
//                connections[id]!!.forEach {
//                    it.send(message)
//                }
//            }
//        } catch (e: Exception) {
//            println(e.localizedMessage)
//        } finally {
//            println("disconnected")
//        }
    }
}