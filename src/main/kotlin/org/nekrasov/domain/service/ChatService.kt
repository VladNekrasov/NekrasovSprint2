package org.nekrasov.domain.service

import kotlinx.datetime.Clock
import org.nekrasov.data.repository.ChatRepository
import org.nekrasov.data.repository.UserChatRepository
import org.nekrasov.data.repository.UserRepository
import org.nekrasov.domain.dto.request.*
import org.nekrasov.domain.models.Chat
import org.nekrasov.domain.models.User
import org.nekrasov.domain.models.UserChat
import org.nekrasov.exceptions.ConflictException
import org.nekrasov.exceptions.NotFoundException

class ChatService(private val chatRepository: ChatRepository,
                  private val userRepository: UserRepository,
                  private val userChatRepository: UserChatRepository
) {
    suspend fun createChat(createChatDto: CreateChatDto): Boolean{
        if (userRepository.read(createChatDto.creatorId) == null){
            throw NotFoundException("Creator not found")
        } else {
            val chat = Chat(
                title = createChatDto.title,
                creatorId = createChatDto.creatorId,
                creationTime = Clock.System.now(),
                deleted = false
            )
            val chatId = chatRepository.create(chat).id
            val userChat = UserChat(
                userId = chat.creatorId,
                chatId = chatId,
                entryTime = chat.creationTime
            )
            return userChatRepository.create(userChat)
        }
    }

    suspend fun getAllChats(page: Long, size: Int): List<Chat> {
        return chatRepository.allChats(page, size)
    }

    suspend fun getChat(idChat: Long): Chat? {
        return chatRepository.read(idChat)
    }

    suspend fun updateChat(updateChatDto: UpdateChatDto): Boolean {
        return chatRepository.read(updateChatDto.idChat)?.let{
            val chat = Chat(
                id = updateChatDto.idChat,
                title = updateChatDto.title,
                creatorId = it.creatorId,
                creationTime = it.creationTime,
                deleted = it.deleted
            )
            if (chat.creatorId == updateChatDto.idUser)
                chatRepository.update(chat)
            else
                throw ConflictException("User is not the creator")
        } ?: throw NotFoundException("Chat not found")
    }

    suspend fun deleteChat(chatDto: ChatDto): Boolean {
        chatRepository.read(chatDto.chatId)?.let {
            if (it.creatorId != chatDto.userId)
                throw ConflictException("User is not the creator")
            userChatRepository.deleteChat(chatDto.chatId)
            return chatRepository.delete(chatDto.chatId)
        } ?: throw NotFoundException("Chat not found")
    }

    suspend fun joinChat(chatDto: ChatDto): Boolean{
        return userRepository.read(chatDto.userId)?.let {
            chatRepository.read(chatDto.chatId)?.let {it2 ->
                if (it2.deleted)
                    throw ConflictException("Chat deleted")
                val userChat = UserChat(
                userId = it.id,
                chatId = it2.id,
                entryTime = Clock.System.now()
                )
                userChatRepository.create(userChat)
            } ?: throw NotFoundException("Chat not found")
        } ?: throw NotFoundException("User not found")
    }

    suspend fun leaveChat(chatDto: ChatDto): Boolean{
        return userRepository.read(chatDto.userId)?.let {
            chatRepository.read(chatDto.chatId)?.let {it2 ->
                if (it2.creatorId != it.id)
                    userChatRepository.delete(it.id, it2.id)
                else
                    throw ConflictException("Creator can't leave chat")
            } ?: throw NotFoundException("Chat not found")
        }  ?: throw NotFoundException("User not found")
    }

    suspend fun checkParticipant(idChat: Long, idUser: Long): Boolean{
        return userChatRepository.getUsersId(idChat).contains(idUser)
    }

    suspend fun getChatParticipants(idChat: Long, page: Long, size: Int): List<User>{
        val participants = mutableListOf<User>()
        val usersId = userChatRepository.getUsersIdPaginated(idChat, page, size)
        usersId.forEach{
            userRepository.read(it)?.let {user ->
                participants.add(user)
            }
        }
        return participants
    }
}