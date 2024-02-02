package org.nekrasov.domain.service

import kotlinx.datetime.Clock
import org.nekrasov.data.repository.ChatRepository
import org.nekrasov.data.repository.UserRepository
import org.nekrasov.domain.dto.request.CreateUserDto
import org.nekrasov.domain.dto.request.LoginUserDto
import org.nekrasov.domain.models.User
import org.nekrasov.utils.checkPassword
import org.nekrasov.utils.hashPassword

class AuthService(private val userRepository: UserRepository, private val chatRepository: ChatRepository) {
    suspend fun createUser(createUserDto: CreateUserDto): Boolean {
        return if (userRepository.readByUsername(createUserDto.username) == null) {
            val user = User(
                username = createUserDto.username,
                firstName = createUserDto.firstName,
                lastName = createUserDto.lastName,
                password = hashPassword(createUserDto.password),
                token = null,
                registrationTime = Clock.System.now(),
                deleted = false
            )
            userRepository.create(user)
            true
        } else
            false
    }

    suspend fun loginUser(loginUserDto: LoginUserDto): String? {
        val user = userRepository.readByUsername(loginUserDto.username)
        if (user == null || !checkPassword(loginUserDto.password, user.password)){
            return null
        }
        val token = hashPassword(loginUserDto.username)
        userRepository.updateToken(user.id, token)
        return token
    }

    suspend fun logoutUser(token: String): Boolean {
        return userRepository.deleteToken(token)
    }

    suspend fun checkToken(token: String): Boolean {
        return userRepository.readByToken(token) != null
    }

    suspend fun checkUser(idConsumer: Long, token: String): Boolean {
        val userConsumer = userRepository.read(idConsumer)
        val produceConsumer = userRepository.readByToken(token)
        return if (userConsumer == null || produceConsumer == null)
            false
        else
            userConsumer == produceConsumer
    }

    suspend fun checkChat(idConsumer: Long, token: String): Boolean {
        val userConsumer = chatRepository.read(idConsumer)
        val produceConsumer = userRepository.readByToken(token)
        return if (userConsumer == null || produceConsumer == null)
            false
        else
            userConsumer.creatorId == produceConsumer.id
    }
}