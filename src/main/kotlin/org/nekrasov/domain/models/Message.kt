package org.nekrasov.domain.models

import java.time.LocalDateTime

data class Message(
    val id: Long,
    val text: String?,
    val chat: Chat,
    val fromId: Long,
    val createTime: LocalDateTime,
)
