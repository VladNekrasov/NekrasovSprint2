package org.nekrasov.domain.service

import kotlinx.datetime.Clock
import org.nekrasov.data.repository.UserRepository
import org.nekrasov.domain.dto.request.CreateUserDto
import org.nekrasov.domain.dto.request.LoginUserDto
import org.nekrasov.domain.dto.response.ResponseLoginUserDto
import org.nekrasov.domain.models.User
import org.nekrasov.utils.ErrorCode
import org.nekrasov.utils.ServiceResult
import org.nekrasov.utils.checkPassword
import org.nekrasov.utils.hashPassword

class AuthService(private val userRepository: UserRepository) {
    suspend fun createUser(createUserDto: CreateUserDto): ServiceResult<Unit> {
        return if (userRepository.readByUsername(createUserDto.username) != null)
            ServiceResult.Error(ErrorCode.DUPLICATE_USERNAME)
        else {
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
            ServiceResult.Success(Unit)
        }
    }

    suspend fun loginUser(loginUserDto: LoginUserDto): ServiceResult<ResponseLoginUserDto> {
        userRepository.readByUsername(loginUserDto.username)?.let {
            if (!checkPassword(loginUserDto.password, it.password))
                return ServiceResult.Error(ErrorCode.INCORRECT_PASSWORD)

            if (it.deleted)
                return ServiceResult.Error(ErrorCode.USER_DELETED)

            val responseLoginUserDto = ResponseLoginUserDto(
                token = hashPassword(it.username),
                id = it.id
            )
            userRepository.updateToken(responseLoginUserDto.id, responseLoginUserDto.token)
            return ServiceResult.Success(responseLoginUserDto)

        } ?: return ServiceResult.Error(ErrorCode.USER_NOT_FOUND)
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
}