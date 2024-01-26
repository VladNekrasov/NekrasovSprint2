package org.nekrasov.domain.service

import org.nekrasov.data.repository.ChatRepository
import org.nekrasov.domain.dto.request.CreateChatDto
import org.nekrasov.domain.models.Chat
import java.time.LocalDateTime

class ChatService(private val chatRepository: ChatRepository) {
    suspend fun createChat(createChatDto: CreateChatDto){
        val chat = Chat(
            id = 0,
            title = createChatDto.title,
            creatorId = createChatDto.creatorId,
            creationTime = LocalDateTime.now(),
            deleted = false
        )
        chatRepository.create(chat)
    }

    suspend fun getChat(id: Long): Chat? {
        return chatRepository.read(id)
    }
}