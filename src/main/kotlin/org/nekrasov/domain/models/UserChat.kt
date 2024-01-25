package org.nekrasov.domain.models

import kotlinx.serialization.Serializable
import org.nekrasov.domain.serializer.LocalDateTimeSerializer
import java.time.LocalDateTime

@Serializable
data class UserChat(
    val userId: Long,
    val chatId: Long,
    @Serializable(with = LocalDateTimeSerializer::class)
    val entryTime: LocalDateTime
)