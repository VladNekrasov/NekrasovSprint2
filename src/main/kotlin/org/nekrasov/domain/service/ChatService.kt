package org.nekrasov.domain.service

import org.nekrasov.data.repository.ChatRepository
import org.nekrasov.data.repository.UserRepository
import org.nekrasov.domain.dto.request.CreateChatDto
import org.nekrasov.domain.models.Chat
import org.nekrasov.domain.models.User
import org.nekrasov.utils.hashPassword
import java.time.LocalDateTime

class ChatService(private val chatRepository: ChatRepository) {
    suspend fun createChat(createChatDto: CreateChatDto){
        val chat = Chat(
            id = 0,
            title = createChatDto.title,
            photo = createChatDto.photo,
            creatorId = createChatDto.creatorId,
            creationTime = LocalDateTime.now(),
            deleted = false
        )
        chatRepository.create(chat)
    }
}