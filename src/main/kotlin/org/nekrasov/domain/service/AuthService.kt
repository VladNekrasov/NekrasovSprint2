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
        if (user == null){
            user = User(
                id = 0,
                username = createUserDto.username,
                firstName = createUserDto.firstName,
                lastName = createUserDto.lastName,
                photo = "basic_photo_path",
                bio = null,
                online = false,
                deleted = false,
                restricted = false,
                premium = false,
                password = hashPassword(createUserDto.password),
                token = null,
                registrationTime = LocalDateTime.now(),
                exitTime = LocalDateTime.now()
            )
            userRepository.create(user)
            return true
        }
        return false
    }

    suspend fun loginUser(readUserDto: ReadUserDto): String? {
        val user = userRepository.readByUsername(readUserDto.username)
        if (user != null && checkPassword(readUserDto.password, user.password)){
            val token = hashPassword(readUserDto.username)
            userRepository.updateToken(user.id, token)
            return token
        }
        return null
    }

    suspend fun logoutUser(token: String): Boolean {
        return userRepository.deleteToken(token)
    }

    suspend fun checkToken(token: String): Boolean {
        val user = userRepository.readByToken(token)
        return user != null
    }
}