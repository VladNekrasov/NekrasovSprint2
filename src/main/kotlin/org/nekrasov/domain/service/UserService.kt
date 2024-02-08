package org.nekrasov.domain.service

import org.nekrasov.data.repository.ChatRepository
import org.nekrasov.data.repository.UserChatRepository
import org.nekrasov.data.repository.UserRepository
import org.nekrasov.domain.dto.request.UpdateUserDto
import org.nekrasov.domain.models.Chat
import org.nekrasov.domain.models.User
import org.nekrasov.utils.ErrorCode
import org.nekrasov.utils.ServiceResult

class UserService(private val userRepository: UserRepository,
                  private val chatRepository: ChatRepository,
                  private val userChatRepository: UserChatRepository
) {
    suspend fun getAllUsers(page: Long, size: Int): List<User>? {
        return if (size > 0 && page > 0)
            userRepository.allUsersPaginated(page, size)
        else
            null
    }

    suspend fun getUser(id: Long): User? {
        return userRepository.read(id)
    }

    suspend fun updateUser(updateUserDto: UpdateUserDto): ServiceResult<Unit> {
        return userRepository.read(updateUserDto.id)?.let {
            val userFind = userRepository.readByUsername(updateUserDto.username)
            if (userFind == null || it.username == updateUserDto.username){
                val user = User(
                    id = updateUserDto.id,
                    username = updateUserDto.username,
                    firstName = updateUserDto.firstName,
                    lastName = updateUserDto.lastName,
                    password = it.password,
                    token = it.token,
                    registrationTime = it.registrationTime,
                    deleted = it.deleted
                )
                userRepository.update(user)
                ServiceResult.Success(Unit)
            } else
                ServiceResult.Error(ErrorCode.DUPLICATE_USERNAME)
        } ?: ServiceResult.Error(ErrorCode.USER_NOT_FOUND)
    }

    suspend fun deleteUser(id: Long): Boolean {
        return userRepository.delete(id)
    }

    suspend fun getUserChats(idUser: Long, page: Long, size: Int): ServiceResult<List<Chat>> {
        if (size<=0 || page<=0)
            return ServiceResult.Error(ErrorCode.INCORRECT_PAGE_SIZE)
        getUser(idUser) ?: return ServiceResult.Error(ErrorCode.USER_NOT_FOUND)
        val chats = mutableListOf<Chat>()
        val chatsId = userChatRepository.getChatsIdPaginated(idUser, page, size)
        chatsId.forEach{
            chatRepository.read(it)?.let {chat ->
                chats.add(chat)
            }
        }
        return ServiceResult.Success(chats)
    }
}