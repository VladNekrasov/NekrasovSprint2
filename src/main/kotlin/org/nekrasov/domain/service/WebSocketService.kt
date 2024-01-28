package org.nekrasov.domain.service

import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import org.nekrasov.data.repository.MessageRepository
import org.nekrasov.domain.dto.request.SendMessageDto
import org.nekrasov.domain.dto.response.ResponseMessageDto
import java.util.concurrent.ConcurrentHashMap

class WebSocketService(private val messageRepository: MessageRepository) {
    private val connections = ConcurrentHashMap<Long, MutableList<DefaultWebSocketServerSession>>()

    fun onConnect(id: Long, currentSession: DefaultWebSocketServerSession): MutableList<DefaultWebSocketServerSession> {
        val room = connections[id]?.apply {
            add(currentSession)
        } ?: mutableListOf(currentSession)
        connections[id]=room
        return room
    }

    suspend fun onMessage(content: String, room: MutableList<DefaultWebSocketServerSession>, token: String){
        val sendMessageDto = Json.decodeFromString<SendMessageDto>(content)
        room.forEach{
            it.send(content)
        }
    }

    suspend fun onClose(id: Long, currentSession: DefaultWebSocketServerSession){
        connections[id]?.run{
            this.remove(currentSession)
        }
    }
}