package org.nekrasov.domain.dto.response

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.nekrasov.domain.models.User

@Serializable
data class ReadUserDto(
    val id: Long,
    val username: String,
    val firstName: String,
    val lastName: String,
    val registrationTime: Instant,
    val deleted: Boolean
)

fun userToReadUserDto(user: User) = ReadUserDto(
    id = user.id,
    username = user.username,
    firstName = user.firstName,
    lastName = user.lastName,
    registrationTime = user.registrationTime,
    deleted = user.deleted
)