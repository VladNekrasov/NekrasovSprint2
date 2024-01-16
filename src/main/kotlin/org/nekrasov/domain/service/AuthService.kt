package org.nekrasov.domain.service

import org.nekrasov.data.repository.UserRepository
import org.nekrasov.domain.dto.request.CreateUserDto
import org.nekrasov.domain.models.User
import java.time.LocalDateTime

class AuthService(private val userRepository: UserRepository) {
    suspend fun createUser(createUserDto: CreateUserDto) {
        val user = User(
            id = 0,
            username = createUserDto.username,
            firstName = createUserDto.firstName,
            lastName = createUserDto.lastName,
            phone = createUserDto.phone,
            email = createUserDto.email,
            photo = "basic_photo_path",
            bio = null,
            statusId = 1,
            deleted = false,
            restricted = false,
            premium = false,
            password = createUserDto.password,
            registrationTime = LocalDateTime.now(),
            exitTime = LocalDateTime.now()
        )
        userRepository.create(user)
    }
}