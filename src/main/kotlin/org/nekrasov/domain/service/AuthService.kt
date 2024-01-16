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
        if (user != null) {
            if (checkPassword(readUserDto.password, user.password)) {
                return "Token"
            }
        }
        return null
    }
}