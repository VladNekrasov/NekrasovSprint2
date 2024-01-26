package org.nekrasov.domain.service

import org.nekrasov.data.repository.UserRepository
import org.nekrasov.domain.dto.request.UpdateUserDto
import org.nekrasov.domain.models.User

class UserService(private val userRepository: UserRepository) {

    suspend fun getAllUsers(): List<User> {
        return userRepository.allUsers()
    }

    suspend fun getUser(id: Long): User? {
        return userRepository.read(id)
    }

    suspend fun updateUser(updateUserDto: UpdateUserDto): Boolean {
        return userRepository.read(updateUserDto.id)?.let {
            if (userRepository.readByUsername(updateUserDto.username) == null){
                val user = User(
                    id = updateUserDto.id,
                    username = updateUserDto.username,
                    firstName = updateUserDto.firstName,
                    lastName = updateUserDto.lastName,
                    password = it.password,
                    token = it.token,
                    registrationTime = it.registrationTime
                )
                userRepository.update(user)
            } else
                false
        } ?: false
    }

    suspend fun deleteUser(id: Long): Boolean {
        return userRepository.delete(id)
    }
}