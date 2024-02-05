package org.nekrasov.domain.dto.response

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.nekrasov.domain.models.Message

@Serializable
data class ResponseMessageDto(
    val id: Long,
    val text: String,
    val fromId: Long,
    val createTime: Instant,
    val deleted: Boolean
)

fun messageToResponseMessageDto(message: Message) = ResponseMessageDto(
    id = message.id,
    text = message.text,
    fromId = message.fromId,
    createTime = message.createTime,
    deleted = message.deleted
)