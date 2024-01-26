package org.nekrasov.domain.service

import org.nekrasov.data.repository.UserRepository
import org.nekrasov.domain.dto.request.CreateUserDto
import org.nekrasov.domain.dto.request.ReadUserDto
import org.nekrasov.domain.models.User
import org.nekrasov.utils.checkPassword
import org.nekrasov.utils.hashPassword
import java.time.LocalDateTime

class AuthService(private val userRepository: UserRepository) {
    suspend fun createUser(createUserDto: CreateUserDto): Boolean {
        var user = userRepository.readByUsername(createUserDto.username)
        if (user != null) {
            return false
        }
        user = User(
            username = createUserDto.username,
            firstName = createUserDto.firstName,
            lastName = createUserDto.lastName,
            password = hashPassword(createUserDto.password),
            token = null,
            registrationTime = LocalDateTime.now(),
            deleted = false
        )
        userRepository.create(user)
        return true
    }

    suspend fun loginUser(readUserDto: ReadUserDto): String? {
        val user = userRepository.readByUsername(readUserDto.username)
        if (user == null || !checkPassword(readUserDto.password, user.password)){
            return null
        }
        val token = hashPassword(readUserDto.username)
        userRepository.updateToken(user.id, token)
        return token
    }

    suspend fun logoutUser(token: String?): Boolean {
        if (token == null)
            return false
        return userRepository.deleteToken(token)
    }

    suspend fun checkToken(token: String?): Boolean {
        if (token == null)
            return false
        val user = userRepository.readByToken(token)
        return user != null
    }

    suspend fun checkUser(idConsumer: Long, token: String): Boolean {
        val userConsumer = userRepository.read(idConsumer)
        val produceConsumer = userRepository.readByToken(token)
        return if (userConsumer == null || produceConsumer == null)
            false
        else
            userConsumer == produceConsumer
    }
}