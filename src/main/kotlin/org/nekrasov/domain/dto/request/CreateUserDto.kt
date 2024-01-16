package org.nekrasov.domain.dto.request

import kotlinx.serialization.Serializable
import org.nekrasov.domain.serializer.LocalDateTimeSerializer
import java.time.LocalDateTime

@Serializable
data class CreateUserDto(
    val username: String,
    val firstName: String,
    val lastName: String,
    val password: String,
)
