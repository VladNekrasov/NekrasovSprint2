package org.nekrasov.domain.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class ReadChatDto (
    val userId: Long,
    val chatId: Long
)