package org.nekrasov.domain.service

import org.nekrasov.data.repository.ChatRepository
import org.nekrasov.data.repository.UserRepository
import org.nekrasov.domain.dto.request.CreateChatDto
import org.nekrasov.domain.dto.response.UserDto
import org.nekrasov.domain.dto.response.userToUserDto
import org.nekrasov.domain.models.Chat
import org.nekrasov.domain.models.User
import org.nekrasov.utils.hashPassword
import java.time.LocalDateTime

class ChatService(private val chatRepository: ChatRepository) {
    suspend fun createChat(createChatDto: CreateChatDto){
        val chat = Chat(
            id = 0,
            title = createChatDto.title,
            creatorId = createChatDto.creatorId,
            creationTime = LocalDateTime.now()
        )
        chatRepository.create(chat)
    }

    suspend fun getChat(id: Long): Chat? {
        return chatRepository.read(id)
    }
}