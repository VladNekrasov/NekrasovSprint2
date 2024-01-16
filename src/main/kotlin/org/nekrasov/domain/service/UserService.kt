package org.nekrasov.domain.service

import org.nekrasov.data.repository.UserRepository
import org.nekrasov.domain.models.User

class UserService(private val userRepository: UserRepository) {
    suspend fun getAllUser() : List<User> {
        TODO()
    }

    suspend fun getUserById() : User? {
        TODO()
    }

    suspend fun createUser(user: User) : User {
        userRepository.create(user)
        return user
    }

    suspend fun updateUser() : User? {
        TODO()
    }

    suspend fun deleteUser() : Boolean {
        TODO()
    }
}