package org.nekrasov.domain.models

import kotlinx.serialization.Serializable
import org.nekrasov.domain.serializer.LocalDateTimeSerializer
import java.time.LocalDateTime

@Serializable
data class Chat(
    val id: Long = 0,
    val title: String,
    val creatorId: Long,
    @Serializable(with = LocalDateTimeSerializer::class)
    val creationTime: LocalDateTime,
    val deleted: Boolean
)