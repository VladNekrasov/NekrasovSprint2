package org.nekrasov.domain.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class JoinLeaveChatDto (
    val userId: Long,
    val chatId: Long
)