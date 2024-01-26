package org.nekrasov.domain.service

import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.util.concurrent.ConcurrentHashMap

class WebSocketService() {
    private val connections = ConcurrentHashMap<Long, MutableList<DefaultWebSocketServerSession>>()

    fun onConnect(id: Long, currentSession: DefaultWebSocketServerSession): MutableList<DefaultWebSocketServerSession> {
        val room = connections[id]?.apply {
            add(currentSession)
        } ?: mutableListOf(currentSession)
        connections[id]=room
        return room
    }

    suspend fun onMessage(content: String, room: MutableList<DefaultWebSocketServerSession>){
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