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
import org.nekrasov.utils.ErrorCode
import org.nekrasov.utils.ServiceResult

class ChatService(private val chatRepository: ChatRepository,
                  private val userRepository: UserRepository,
                  private val userChatRepository: UserChatRepository
) {
    suspend fun createChat(createChatDto: CreateChatDto): ServiceResult<Unit> {
        userRepository.read(createChatDto.creatorId) ?: return ServiceResult.Error(ErrorCode.USER_NOT_FOUND)
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
        return if (userChatRepository.create(userChat))
            ServiceResult.Success(Unit)
        else
            ServiceResult.Error(ErrorCode.DUPLICATE_USER_IN_CHAT)
    }

    suspend fun getAllChats(page: Long, size: Int): List<Chat>? {
        return if (size > 0 && page > 0)
            chatRepository.allChats(page, size)
        else
            null
    }

    suspend fun getChat(idChat: Long): Chat? {
        return chatRepository.read(idChat)
    }

    suspend fun updateChat(updateChatDto: UpdateChatDto): ServiceResult<Unit> {
        return chatRepository.read(updateChatDto.idChat)?.let{
            val chat = Chat(
                id = updateChatDto.idChat,
                title = updateChatDto.title,
                creatorId = it.creatorId,
                creationTime = it.creationTime,
                deleted = it.deleted
            )
            if (chat.creatorId == updateChatDto.idUser) {
                chatRepository.update(chat)
                ServiceResult.Success(Unit)
            }
            else
                ServiceResult.Error(ErrorCode.USER_NOT_CREATOR)
        } ?: ServiceResult.Error(ErrorCode.CHAT_NOT_FOUND)
    }

    suspend fun deleteChat(chatDto: ChatDto): ServiceResult<Unit> {
        return chatRepository.read(chatDto.chatId)?.let {
            if (it.creatorId == chatDto.userId){
                chatRepository.delete(chatDto.chatId)
                ServiceResult.Success(Unit)
            } else
                ServiceResult.Error(ErrorCode.USER_NOT_CREATOR)
        } ?: ServiceResult.Error(ErrorCode.CHAT_NOT_FOUND)
    }

    suspend fun joinChat(chatDto: ChatDto): ServiceResult<Unit> {
        return userRepository.read(chatDto.userId)?.let {
            chatRepository.read(chatDto.chatId)?.let {it2 ->
                if (it2.deleted)
                    ServiceResult.Error(ErrorCode.CHAT_DELETED)
                val userChat = UserChat(
                userId = it.id,
                chatId = it2.id,
                entryTime = Clock.System.now()
                )
                if (userChatRepository.create(userChat))
                    ServiceResult.Success(Unit)
                else
                    ServiceResult.Error(ErrorCode.DUPLICATE_USER_IN_CHAT)
            } ?: ServiceResult.Error(ErrorCode.CHAT_NOT_FOUND)
        } ?: ServiceResult.Error(ErrorCode.USER_NOT_FOUND)
    }

    suspend fun leaveChat(chatDto: ChatDto): ServiceResult<Unit> {
        return userRepository.read(chatDto.userId)?.let {
            chatRepository.read(chatDto.chatId)?.let {it2 ->
                if (it2.creatorId != it.id)
                {
                    userChatRepository.delete(it.id, it2.id)
                    ServiceResult.Success(Unit)
                }
                else
                    ServiceResult.Error(ErrorCode.CREATOR_LEAVE_CHAT)
            } ?: ServiceResult.Error(ErrorCode.CHAT_NOT_FOUND)
        }  ?: ServiceResult.Error(ErrorCode.USER_NOT_FOUND)
    }

    suspend fun checkParticipant(idChat: Long, idUser: Long): Boolean{
        return userChatRepository.getUsersId(idChat).contains(idUser)
    }

    suspend fun getChatParticipants(chatDto: ChatDto, page: Long, size: Int): ServiceResult<List<User>>{
        if (size<=0 || page<=0)
            return ServiceResult.Error(ErrorCode.INCORRECT_PAGE_SIZE)
        if (!checkParticipant(chatDto.chatId, chatDto.userId))
            return ServiceResult.Error(ErrorCode.USER_NOT_PARTICIPANT)
        getChat(chatDto.chatId) ?: return ServiceResult.Error(ErrorCode.CHAT_NOT_FOUND)

        val participants = mutableListOf<User>()
        val usersId = userChatRepository.getUsersIdPaginated(chatDto.chatId, page, size)
        usersId.forEach{
            userRepository.read(it)?.let {user ->
                participants.add(user)
            }
        }
        return ServiceResult.Success(participants)
    }
}