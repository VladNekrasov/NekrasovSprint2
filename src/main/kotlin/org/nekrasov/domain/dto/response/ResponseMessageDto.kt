package org.nekrasov.domain.dto.response

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ResponseMessageDto(
    val id: Long = 0,
    val text: String?,
    val chat: Long,
    val fromId: Long,
    val createTime: Instant
)
