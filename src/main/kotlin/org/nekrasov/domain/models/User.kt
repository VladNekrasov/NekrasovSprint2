package org.nekrasov.domain.models

import com.typesafe.config.Optional
import kotlinx.serialization.Serializable
import org.nekrasov.domain.serializer.LocalDateTimeSerializer
import java.time.LocalDateTime
@Serializable
data class User(
    val id: Long,
    val username: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val email: String,
    val photo: String,
    val bio: String?,
    val online: Boolean,
    val deleted: Boolean,
    val restricted: Boolean,
    val premium: Boolean,
    val password: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val registrationTime: LocalDateTime,
    @Serializable(with = LocalDateTimeSerializer::class)
    val exitTime: LocalDateTime?
)
