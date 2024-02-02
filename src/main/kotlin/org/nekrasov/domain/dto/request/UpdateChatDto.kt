package org.nekrasov.domain.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateChatDto (
    val idChat: Long,
    val title: String,
    val idUser: Long
)