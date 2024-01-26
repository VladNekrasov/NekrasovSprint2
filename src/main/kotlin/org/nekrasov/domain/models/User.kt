package org.nekrasov.domain.models

import kotlinx.serialization.Serializable
import org.nekrasov.domain.serializer.LocalDateTimeSerializer
import java.time.LocalDateTime

@Serializable
data class User(
    val id: Long = 0,
    val username: String,
    val firstName: String,
    val lastName: String,
    val password: String,
    val token: String?,
    @Serializable(with = LocalDateTimeSerializer::class)
    val registrationTime: LocalDateTime,
    val deleted: Boolean
)
