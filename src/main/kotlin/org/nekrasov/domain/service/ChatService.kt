package org.nekrasov.domain.service

import org.nekrasov.data.repository.ChatRepository
import org.nekrasov.data.repository.UserChatRepository
import org.nekrasov.data.repository.UserRepository
import org.nekrasov.domain.dto.request.CreateChatDto
import org.nekrasov.domain.dto.request.UpdateChatDto
import org.nekrasov.domain.dto.request.UpdateUserDto
import org.nekrasov.domain.models.Chat
import org.nekrasov.domain.models.User
import org.nekrasov.domain.models.UserChat
import java.time.LocalDateTime

class ChatService(private val chatRepository: ChatRepository,
                  private val userRepository: UserRepository,
                  private val userChatRepository: UserChatRepository
) {
    suspend fun createChat(createChatDto: CreateChatDto): Boolean{
        return if (userRepository.read(createChatDto.creatorId) != null){
            val chat = Chat(
                id = 0,
                title = createChatDto.title,
                creatorId = createChatDto.creatorId,
                creationTime = LocalDateTime.now(),
                deleted = false
            )
            val chatId = chatRepository.create(chat).id
            val userChat = UserChat(
                userId = chat.creatorId,
                chatId = chatId,
                entryTime = chat.creationTime
            )
            userChatRepository.create(userChat)
            true
        } else {
            false
        }
    }

    suspend fun getAllChats(): List<Chat> {
        return chatRepository.allChats()
    }

    suspend fun getChat(id: Long): Chat? {
        return chatRepository.read(id)
    }

    suspend fun updateChat(updateChatDto: UpdateChatDto): Boolean {
        return chatRepository.read(updateChatDto.id)?.let{
            val chat = Chat(
                id = updateChatDto.id,
                title = updateChatDto.title,
                creatorId = it.creatorId,
                creationTime = it.creationTime,
                deleted = it.deleted
            )
            chatRepository.update(chat)
        } ?: false
    }

    suspend fun deleteChat(id: Long): Boolean {
        userChatRepository.deleteChat(id)
        return chatRepository.delete(id)
    }

    suspend fun joinChat(idChat: Long, token: String): Boolean{
        return userRepository.readByToken(token)?.let {
            val userChat = UserChat(
                userId = it.id,
                chatId = idChat,
                entryTime = LocalDateTime.now()
            )
            userChatRepository.create(userChat)
        } ?: false
    }

    suspend fun leaveChat(idChat: Long, token: String): Boolean{
        return userRepository.readByToken(token)?.let {
            userChatRepository.delete(it.id, idChat)
        } ?: false
    }

    suspend fun getChatParticipants(idChat: Long): List<User>{
        val participants = mutableListOf<User>()
        val usersId = userChatRepository.getUsersId(idChat)
        usersId.forEach{
            userRepository.read(it)?.let {user ->
                participants.add(user)
            }
        }
        return participants
    }
}