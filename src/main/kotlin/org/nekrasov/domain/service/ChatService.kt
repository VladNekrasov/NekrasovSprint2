package org.nekrasov.domain.service

import org.nekrasov.data.repository.ChatRepository
import org.nekrasov.data.repository.UserChatRepository
import org.nekrasov.data.repository.UserRepository
import org.nekrasov.domain.dto.request.CreateChatDto
import org.nekrasov.domain.dto.request.JoinLeaveChatDto
import org.nekrasov.domain.dto.request.ReadChatDto
import org.nekrasov.domain.dto.request.UpdateChatDto
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

    suspend fun getChat(readChatDto: ReadChatDto): Chat? {
        return userRepository.read(readChatDto.userId)?.let {
            chatRepository.read(readChatDto.chatId)?.let { it2->
               if (userChatRepository.getUsersId(it2.id).contains(it.id))
                   chatRepository.read(it2.id)
               else
                   null
            }
        }
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

    suspend fun joinChat(joinLeaveChatDto: JoinLeaveChatDto): Boolean{
        return userRepository.read(joinLeaveChatDto.userId)?.let {
            chatRepository.read(joinLeaveChatDto.chatId)?.let {it2 ->
                val userChat = UserChat(
                userId = it.id,
                chatId = it2.id,
                entryTime = LocalDateTime.now()
            )
            userChatRepository.create(userChat)
            } ?: false
        } ?: false
    }

    suspend fun leaveChat(joinLeaveChatDto: JoinLeaveChatDto): Boolean{
        return userRepository.read(joinLeaveChatDto.userId)?.let {
            chatRepository.read(joinLeaveChatDto.chatId)?.let {it2 ->
                if (it2.creatorId != it.id)
                    userChatRepository.delete(it.id, it2.id)
                else
                    false
            } ?: false
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