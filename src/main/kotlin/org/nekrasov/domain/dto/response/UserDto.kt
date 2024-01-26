package org.nekrasov.domain.dto.response

import kotlinx.serialization.Serializable
import org.nekrasov.domain.models.User
import org.nekrasov.domain.serializer.LocalDateTimeSerializer
import java.time.LocalDateTime

@Serializable
data class UserDto(
    val id: Long,
    val username: String,
    val firstName: String,
    val lastName: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val registrationTime: LocalDateTime,
    val deleted: Boolean
)

fun userToUserDto(user: User) = UserDto(
    id = user.id,
    username = user.username,
    firstName = user.firstName,
    lastName = user.lastName,
    registrationTime = user.registrationTime,
    deleted = user.deleted
)