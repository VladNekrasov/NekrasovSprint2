package org.nekrasov.domain.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateChatDto (
    val id: Long,
    val title: String
)