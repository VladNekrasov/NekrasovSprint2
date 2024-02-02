package org.nekrasov.domain.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class DeleteChatDto (
    val idChat: Long,
    val idUser: Long
)