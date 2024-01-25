package org.nekrasov.domain.dto.request

import kotlinx.serialization.Serializable
import org.nekrasov.domain.serializer.LocalDateTimeSerializer
import java.time.LocalDateTime

@Serializable
data class SendMessageDto(
    val id: Long,
    val text: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createTime: LocalDateTime
)
