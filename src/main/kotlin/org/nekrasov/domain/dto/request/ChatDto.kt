package org.nekrasov.domain.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class ChatDto (
    val userId: Long,
    val chatId: Long
)