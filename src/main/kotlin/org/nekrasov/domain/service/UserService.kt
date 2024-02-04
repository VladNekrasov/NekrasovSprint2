package org.nekrasov.domain.service

import org.nekrasov.data.repository.ChatRepository
import org.nekrasov.data.repository.UserChatRepository
import org.nekrasov.data.repository.UserRepository
import org.nekrasov.domain.dto.request.UpdateUserDto
import org.nekrasov.domain.models.Chat
import org.nekrasov.domain.models.User
import org.nekrasov.exceptions.ConflictException
import org.nekrasov.exceptions.NotFoundException

class UserService(private val userRepository: UserRepository,
                  private val chatRepository: ChatRepository,
                  private val userChatRepository: UserChatRepository
) {
    suspend fun getAllUsers(): List<User> {
        return userRepository.allUsers()
    }

    suspend fun getAllUsersPaginated(page: Long, size: Int): List<User> {
        return userRepository.allUsersPaginated(page, size)
    }

    suspend fun getUser(id: Long): User? {
        return userRepository.read(id)
    }

    suspend fun updateUser(updateUserDto: UpdateUserDto): Boolean {
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
            } else
                throw ConflictException("Duplicate username")
        } ?: throw NotFoundException("User not found")
    }

    suspend fun deleteUser(id: Long): Boolean {
        userChatRepository.deleteUser(id)
        return userRepository.delete(id)
    }

    suspend fun getUserChats(idUser: Long): List<Chat>{
        val chats = mutableListOf<Chat>()
        val chatsId = userChatRepository.getChatsId(idUser)
        chatsId.forEach{
            chatRepository.read(it)?.let {chat ->
                chats.add(chat)
            }
        }
        return chats
    }
}