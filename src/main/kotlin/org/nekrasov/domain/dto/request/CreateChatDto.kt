package org.nekrasov.domain.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateChatDto (
    val title: String,
    val creatorId: Long
)