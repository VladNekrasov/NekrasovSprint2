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
        if (userRepository.read(createChatDto.creatorId) != null){
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
        } else {
            throw NotFoundException("Creator not found")
        }
    }

    suspend fun getAllChats(): List<Chat> {
        return chatRepository.allChats()
    }

    suspend fun getChat(idChat: Long): Chat? {
//        return userRepository.read(readChatDto.userId)?.let {
//            chatRepository.read(readChatDto.chatId)?.let { it2->
//               if (userChatRepository.getUsersId(it2.id).contains(it.id))
//                   chatRepository.read(it2.id)
//               else
//                   null
//            }
//        }
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

    suspend fun deleteChat(deleteChatDto: DeleteChatDto): Boolean {
        chatRepository.read(deleteChatDto.idChat)?.let {
            if (it.creatorId != deleteChatDto.idUser)
                throw ConflictException("User is not the creator")
            userChatRepository.deleteChat(deleteChatDto.idChat)
            return chatRepository.delete(deleteChatDto.idChat)
        } ?: throw NotFoundException("Chat not found")
    }

    suspend fun joinChat(joinLeaveChatDto: JoinLeaveChatDto): Boolean{
        return userRepository.read(joinLeaveChatDto.userId)?.let {
            chatRepository.read(joinLeaveChatDto.chatId)?.let {it2 ->
                val userChat = UserChat(
                userId = it.id,
                chatId = it2.id,
                entryTime = Clock.System.now()
            )
            userChatRepository.create(userChat)
            } ?: throw NotFoundException("Chat not found")
        } ?: throw NotFoundException("User not found")
    }

    suspend fun leaveChat(joinLeaveChatDto: JoinLeaveChatDto): Boolean{
        return userRepository.read(joinLeaveChatDto.userId)?.let {
            chatRepository.read(joinLeaveChatDto.chatId)?.let {it2 ->
                if (it2.creatorId != it.id)
                    userChatRepository.delete(it.id, it2.id)
                else
                    throw ConflictException("Creator can't leave chat")
            } ?: throw NotFoundException("Chat not found")
        }  ?: throw NotFoundException("User not found")
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