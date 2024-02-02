package org.nekrasov.domain.service

import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import org.nekrasov.data.repository.MessageRepository
import org.nekrasov.domain.dto.request.SendMessageDto
import org.nekrasov.domain.models.Message
import java.util.concurrent.ConcurrentHashMap

class WebSocketService(private val messageRepository: MessageRepository) {
    private val connections = ConcurrentHashMap<Long, MutableList<DefaultWebSocketServerSession>>()

    fun onConnect(id: Long, currentSession: DefaultWebSocketServerSession){
        val room = connections[id]?.apply {
            add(currentSession)
        } ?: mutableListOf(currentSession)
        connections[id]=room
    }

    suspend fun onMessage(content: String, idChat: Long, currentSession: DefaultWebSocketServerSession){
        val sendMessageDto = Json.decodeFromString<SendMessageDto>(content)
        val message = Message(
            text = sendMessageDto.text,
            chat = idChat,
            fromId = sendMessageDto.userId,
            createTime = sendMessageDto.createTime,
            deleted = false
        )
        messageRepository.create(message)
        connections[idChat]?.forEach{
            if (it != currentSession)
                it.send(content)
        }
    }

    fun onClose(id: Long, currentSession: DefaultWebSocketServerSession){
        connections[id]?.run{
            this.remove(currentSession)
        }
    }
}