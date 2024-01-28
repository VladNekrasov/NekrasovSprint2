package org.nekrasov.domain.dto.response

import kotlinx.serialization.Serializable
import org.nekrasov.domain.serializer.LocalDateTimeSerializer
import java.time.LocalDateTime

@Serializable
data class ResponseMessageDto(
    val id: Long = 0,
    val text: String?,
    val chat: Long,
    val fromId: Long,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createTime: LocalDateTime,
)
