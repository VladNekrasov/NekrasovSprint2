package org.nekrasov.domain.dto.request

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class SendMessageDto(
    val userId: Long,
    val text: String?,
    val createTime: Instant
)
