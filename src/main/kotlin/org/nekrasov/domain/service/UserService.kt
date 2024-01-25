package org.nekrasov.domain.service

import org.nekrasov.data.repository.UserRepository
import org.nekrasov.domain.dto.response.UserDto
import org.nekrasov.domain.dto.response.userToUserDto
import org.nekrasov.domain.models.User

class UserService(private val userRepository: UserRepository) {
    suspend fun getAllUsers(): List<UserDto> {
        return userRepository.allUsers().map(::userToUserDto)
    }

    suspend fun getUser(id: Long): UserDto? {
        val user = userRepository.read(id) ?: return null
        return userToUserDto(user)
    }
}